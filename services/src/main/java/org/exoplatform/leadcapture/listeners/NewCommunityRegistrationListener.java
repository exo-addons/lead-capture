package org.exoplatform.leadcapture.listeners;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.exoplatform.social.core.activity.model.ExoSocialActivity;

import static org.exoplatform.leadcapture.Utils.*;

public class NewCommunityRegistrationListener extends Listener<Map<String, String>, String> {

  private static final Log               LOG = ExoLogger.getLogger(NewCommunityRegistrationListener.class);

  private LCMailService                  lcMailService;

  private MailTemplatesManagementService mailTemplatesManagementService;

  protected LeadDAO                      leadDAO;

  private LeadCaptureSettingsService     leadCaptureSettingsService;

  public NewCommunityRegistrationListener(LCMailService lcMailService,
                                          LeadDAO leadDAO,
                                          MailTemplatesManagementService mailTemplatesManagementService,
                                          LeadCaptureSettingsService leadCaptureSettingsService) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
    this.leadDAO = leadDAO;
    this.leadCaptureSettingsService = leadCaptureSettingsService;

  }

  @Override
  public void onEvent(Event<Map<String, String>, String> event) throws Exception {
    try {
      String email = (String) event.getSource().get("email");
      String firstName = (String) event.getSource().get("firstName");
      String lastName = (String) event.getSource().get("lastName");
      String captureMethod = (String) event.getSource().get("captureMethod");
      String captureSourceInfo = (String) event.getSource().get("captureSourceInfo");
      String referer = (String) event.getSource().get("referer");
      String userName = (String) event.getSource().get("userName");
      String leadSource = (String) event.getSource().get("leadSource");
      String regMethod = (String) event.getSource().get("registrationMethod");
      String country = (String) event.getSource().get("country");
      String phone = (String) event.getSource().get("phone");
      long createdDate = Long.parseLong(event.getSource().get("createdDate"));
      LeadEntity lead = leadDAO.getLeadByMail(email);
      if (lead != null) {
        lead.setFirstName(firstName);
        lead.setLastName(lastName);
        lead.setUpdatedDate(new Date());
        lead.setCommunityRegistration(true);
        lead.setCommunityRegistrationDate(new Date(createdDate));
        lead.setCommunityUserName(userName);
        lead.setCommunityRegistrationMethod(regMethod);
        lead.setPhone(phone);
        lead.setCountry(country);
        lead.setPersonSource(getLeadSource(lead));
        lead.setGeographiqueZone(getGeoZone(lead));
        leadDAO.update(lead);
      } else {
        lead = new LeadEntity();
        lead.setMail(email);
        lead.setFirstName(firstName);
        lead.setLastName(lastName);
        lead.setCaptureMethod(regMethod);
        lead.setCaptureSourceInfo(captureSourceInfo);
        lead.setOriginalReferrer(referer);
        lead.setPersonSource(leadSource);
        lead.setUpdatedDate(new Date());
        lead.setCreatedDate(new Date());
        lead.setCommunityRegistration(true);
        lead.setCommunityRegistrationDate(new Date(createdDate));
        lead.setCommunityUserName(userName);
        lead.setCommunityRegistrationMethod(regMethod);
        lead.setCountry(country);
        lead.setStatus(LEAD_DEFAULT_STATUS);
        lead.setPersonSource(getLeadSource(lead));
        lead.setGeographiqueZone(getGeoZone(lead));
        leadDAO.create(lead);
        LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_name:{},form_name:Community registration\"",
                 lead.getFirstName() + " " + lead.getLastName());
        LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
        if (settings.getUserExperienceSpace() != null && settings.getUserExperienceBotUserName() != null) {
          ExoSocialActivity activity = Utils.createActivity(lead);
          if (activity != null) {
            lead.setActivityId(activity.getId());
          }
        }
        leadDAO.update(lead);

      }
      List<MailTemplateEntity> templates = mailTemplatesManagementService.getTemplatesbyEvent("newCommunityRegistration");
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

      LOG.info("Lead {} has been associated to the community user {}", lead.getId(), userName);
    } catch (Exception e) {
      LOG.info("Error occured when trying to synchronise user from Tribe");
    }

  }
}
