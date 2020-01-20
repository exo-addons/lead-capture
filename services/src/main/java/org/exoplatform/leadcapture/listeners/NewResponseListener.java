package org.exoplatform.leadcapture.listeners;

import static org.exoplatform.leadcapture.Utils.LEAD_SYNCHRONIZE_EVENT;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class NewResponseListener extends Listener<LeadEntity, ResponseEntity> {

  private static final Log               LOG = ExoLogger.getLogger(NewResponseListener.class);

  private LCMailService                  lcMailService;

  private MailTemplatesManagementService mailTemplatesManagementService;

  private ListenerService                listenerService;

  private FieldDAO                       fieldDAO;

  private LeadCaptureSettingsService     leadCaptureSettingsService;

  private LeadsManagementService         leadsManagementService;

  public NewResponseListener(LCMailService lcMailService,
                             MailTemplatesManagementService mailTemplatesManagementService,
                             LeadsManagementService leadsManagementService,
                             FieldDAO fieldDAO,
                             ListenerService listenerService,
                             LeadCaptureSettingsService leadCaptureSettingsService) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
    this.leadsManagementService = leadsManagementService;
    this.fieldDAO = fieldDAO;
    this.listenerService = listenerService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  @Override
  public void onEvent(Event<LeadEntity, ResponseEntity> event) throws Exception {
    LeadEntity lead = event.getSource();
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    ResponseEntity responseEntity = event.getData();
    if (!settings.isLeadManagementServer()) {
      FormInfo formInfo = new FormInfo(null,
                                       leadsManagementService.toLeadDto(lead),
                                       leadsManagementService.toResponseDto(responseEntity));
      listenerService.broadcast(LEAD_SYNCHRONIZE_EVENT, formInfo, "");
    }
    if (settings.isMailingEnabled()) {
      List<MailTemplateEntity> templates = mailTemplatesManagementService.getTemplatesbyEvent("newResponse");
      for (MailTemplateEntity template : templates) {
        MailContentDTO content = null;
        MailTemplateDTO mailTemplateDTO = mailTemplatesManagementService.toMailTemplateDTO(template);
        if (mailTemplateDTO.getContents().size() > 0) {
          content = Utils.getContentForMail(mailTemplateDTO, lead);
          if (content != null && shouldBeSend(template, responseEntity)) {
            lcMailService.sendMail(content.getContent(), content.getSubject(), lead);
            LOG.info("Mail sent to {}", lead.getMail());
          }
        }

      }
    }
  }

  private boolean shouldBeSend(MailTemplateEntity template, ResponseEntity responseEntity) {
    if (StringUtils.isEmpty(template.getForm()))
      return true;
    if (StringUtils.isEmpty(template.getField()) && responseEntity.getFormEntity().getName().equals(template.getForm()))
      return true;
    if (responseEntity.getFormEntity().getName().equals(template.getForm())) {
      List<FieldEntity> fields = fieldDAO.getFileldsByResponse(responseEntity.getId());
      for (FieldEntity fieldEntity : fields) {
        if (fieldEntity.getValue().equals(template.getField()))
          return true;
      }
    }
    return false;
  }
}
