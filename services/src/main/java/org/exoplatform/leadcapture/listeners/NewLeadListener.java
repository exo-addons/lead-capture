package org.exoplatform.leadcapture.listeners;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dto.FieldDTO;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.Query;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.dto.TaskDto;
import org.exoplatform.task.util.TaskUtil;

import static org.exoplatform.leadcapture.Utils.*;

public class NewLeadListener extends Listener<LeadEntity, String> {

  private static final Log               LOG = ExoLogger.getLogger(NewLeadListener.class);

  private LCMailService                  lcMailService;

  private MailTemplatesManagementService mailTemplatesManagementService;

  private OrganizationService            organizationService;

  private LeadDAO                        leadDAO;

  private LeadCaptureSettingsService     leadCaptureSettingsService;

  private LeadsManagementService         leadsManagementService;

  public NewLeadListener(LCMailService lcMailService,
                         MailTemplatesManagementService mailTemplatesManagementService,
                         OrganizationService organizationService,
                         LeadsManagementService leadsManagementService,
                         LeadDAO leadDAO,
                         LeadCaptureSettingsService leadCaptureSettingsService) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
    this.organizationService = organizationService;
    this.leadDAO = leadDAO;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
    this.leadsManagementService = leadsManagementService;
  }

  @Override
  public void onEvent(Event<LeadEntity, String> event) throws Exception {
    LeadEntity lead = event.getSource();
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    if (StringUtils.isEmpty(lead.getCommunityUserName())) {
      Query query = new Query();
      query.setEmail(lead.getMail());
      ListAccess<User> users = organizationService.getUserHandler().findUsersByQuery(query);
      if (users.getSize() > 0) {
        User communityUser = users.load(0, 1)[0];
        lead.setCommunityRegistration(true);
        lead.setCommunityUserName(communityUser.getUserName());
        lead.setCommunityRegistrationDate(communityUser.getCreatedDate());
        LOG.info("Lead {} has been associated to the community user {}", lead.getId(), communityUser.getUserName());
      }
    }

    if (settings.getUserExperienceSpace() != null && settings.getUserExperienceBotUserName() != null) {
      ExoSocialActivity activity = Utils.createActivity(lead);
      if (activity != null) {
        lead.setActivityId(activity.getId());
      }
      if (lead.getStatus().equals(LEAD_OPEN_STATUS)) {
        TaskDto task = leadsManagementService.createTask(lead);
        if (task != null) {
          lead.setTaskId(task.getId());
          lead.setTaskUrl(leadsManagementService.buildTaskURL(task));
          LOG.info("new task with id = {} has been associated to the lead {}", task.getId(), lead.getId());
        }
      }
    }
    leadDAO.update(lead);
    List<MailTemplateEntity> templates = mailTemplatesManagementService.getTemplatesbyEvent("newLead");
    for (MailTemplateEntity template : templates) {
      MailContentDTO content = null;
      MailTemplateDTO mailTemplateDTO = mailTemplatesManagementService.toMailTemplateDTO(template);
      if (mailTemplateDTO.getContents().size() > 0) {
        content = Utils.getContentForMail(mailTemplateDTO, lead);
        if (content != null) {
          lcMailService.sendMail(content.getContent(), content.getSubject(), lead, null, null);
          LOG.info("service=lead-capture operation=send_mail_to_lead parameters=\"lead_id:{},lead_name:{},mail_template_id:{},mail_template_name:{},reason: NewLead\"",
                   lead.getId(),
                   lead.getFirstName() + " " + lead.getLastName(),
                   mailTemplateDTO.getId(),
                   mailTemplateDTO.getName());
        }
      }

    }
  }
}
