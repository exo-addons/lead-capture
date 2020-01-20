package org.exoplatform.leadcapture.listeners;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.service.ProjectService;
import org.exoplatform.task.service.StatusService;
import org.exoplatform.task.service.TaskService;
import org.exoplatform.task.util.TaskUtil;

public class NewLeadListener extends Listener<LeadEntity, String> {

  private static final Log               LOG = ExoLogger.getLogger(NewLeadListener.class);

  private LCMailService                  lcMailService;

  private MailTemplatesManagementService mailTemplatesManagementService;

  private OrganizationService            organizationService;

  private ProjectService                 projectService;

  private StatusService                  statusService;

  private TaskService                    taskService;

  private LeadDAO                        leadDAO;

  private LeadCaptureSettingsService     leadCaptureSettingsService;

  public NewLeadListener(LCMailService lcMailService,
                         MailTemplatesManagementService mailTemplatesManagementService,
                         OrganizationService organizationService,
                         ProjectService projectService,
                         TaskService taskService,
                         StatusService statusService,
                         LeadDAO leadDAO,
                         LeadCaptureSettingsService leadCaptureSettingsService) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
    this.organizationService = organizationService;
    this.projectService = projectService;
    this.taskService = taskService;
    this.statusService = statusService;
    this.leadDAO = leadDAO;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  @Override
  public void onEvent(Event<LeadEntity, String> event) throws Exception {
    LeadEntity lead = event.getSource();
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    if (settings.isLeadManagementServer()) {
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
      task.setDescription("");
      task.setStatus(status);
      task.setCreatedBy(leadCaptureSettingsService.getSettings().getMarketingBotUserName());
      task.setCreatedTime(new Date());
      task = taskService.createTask(task);
      lead.setTaskId(task.getId());
      lead.setTaskUrl(TaskUtil.buildTaskURL(task));
      leadDAO.update(lead);
    }
    if (settings.isMailingEnabled()) {
      List<MailTemplateEntity> templates = mailTemplatesManagementService.getTemplatesbyEvent("newLead");
      for (MailTemplateEntity template : templates) {
        MailContentDTO content = null;
        MailTemplateDTO mailTemplateDTO = mailTemplatesManagementService.toMailTemplateDTO(template);
        if (mailTemplateDTO.getContents().size() > 0) {
          content = Utils.getContentForMail(mailTemplateDTO, lead);
          if (content != null) {
            lcMailService.sendMail(content.getContent(), content.getSubject(), lead);
          }
        }

      }
    }
  }
}
