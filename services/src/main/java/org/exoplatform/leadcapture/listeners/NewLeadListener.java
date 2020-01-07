package org.exoplatform.leadcapture.listeners;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.MailTemplatesManagement;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.task.domain.Label;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.exoplatform.task.exception.UnAuthorizedOperationException;
import org.exoplatform.task.service.*;
import org.exoplatform.task.util.DateUtil;
import org.exoplatform.task.util.ProjectUtil;
import org.exoplatform.task.util.TaskUtil;
import static org.exoplatform.leadcapture.Constants.*;

public class NewLeadListener extends Listener<LeadEntity, String> {

  private static final Log        LOG = ExoLogger.getLogger(NewLeadListener.class);

  private LCMailService           lcMailService;

  private MailTemplatesManagement mailTemplatesManagement;

  private OrganizationService     organizationService;

  private ProjectService projectService;

  private StatusService statusService;

  private TaskService taskService;

  private LeadDAO                 leadDAO;

  public NewLeadListener(LCMailService lcMailService,
                         MailTemplatesManagement mailTemplatesManagement,
                         OrganizationService organizationService,
                         ProjectService projectService,
                         TaskService taskService,
                         StatusService statusService,
                         LeadDAO leadDAO) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagement = mailTemplatesManagement;
    this.organizationService = organizationService;
    this.projectService = projectService;
    this.taskService = taskService;
    this.statusService = statusService;
    this.leadDAO = leadDAO;
  }

  @Override
  public void onEvent(Event<LeadEntity, String> event) throws Exception {
    LeadEntity lead = event.getSource();
    if (StringUtils.isEmpty(lead.getCommunityUserName())) {
      Query query = new Query();
      query.setEmail(lead.getMail());
      ListAccess<User> users = organizationService.getUserHandler().findUsersByQuery(query);
      if (users.getSize() > 0) {
        User communityUser = users.load(0, 1)[0];
        lead.setCommunityUserName(communityUser.getUserName());
        lead.setCommunityRegistrationDate(communityUser.getCreatedDate().getTime());
      }
    }

    ExoSocialActivity activity = Utils.createActivity(lead);
    lead.setActivityId(activity.getId());
    Status status = statusService.getDefaultStatus(Utils.getTaskProject().getId());
    Task task = new Task();
    task.setTitle(lead.getMail());
    task.setDescription("description");
    task.setStatus(status);
    task.setCreatedBy(DEFAULT_LEAD_CAPTURE_BOT_NAME);
    task.setCreatedTime(new Date());
    task = taskService.createTask(task);
    lead.setTaskId(task.getId());
    lead.setTaskUrl(TaskUtil.buildTaskURL(task));
    leadDAO.update(lead);

    List<MailTemplateEntity> templates = mailTemplatesManagement.getTemplatesbyEvent("newLead");
    for (MailTemplateEntity template : templates) {
      MailContentDTO content = null;
      MailTemplateDTO mailTemplateDTO = mailTemplatesManagement.toMailTemplateDTO(template);
      if (mailTemplateDTO.getContents().size() > 0) {
        content = Utils.getContentForMail(mailTemplateDTO, lead);
        if (content != null) {
          lcMailService.sendMail(content.getContent(), content.getSubject(), lead);
        }
      }

    }
  }
}
