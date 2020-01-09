package org.exoplatform.leadcapture.listeners;

import java.util.List;

import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class NewCommunityRegistrationListener extends Listener<LeadEntity, String> {

  private static final Log        LOG = ExoLogger.getLogger(NewCommunityRegistrationListener.class);

  private LCMailService           lcMailService;

  private MailTemplatesManagementService mailTemplatesManagementService;

  public NewCommunityRegistrationListener(LCMailService lcMailService, MailTemplatesManagementService mailTemplatesManagementService) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
  }

  @Override
  public void onEvent(Event<LeadEntity, String> event) throws Exception {
    LeadEntity lead = event.getSource();
    List<MailTemplateEntity> templates = mailTemplatesManagementService.getTemplatesbyEvent("newCommunityRegistration");
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
