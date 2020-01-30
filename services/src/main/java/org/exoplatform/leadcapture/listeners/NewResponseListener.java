package org.exoplatform.leadcapture.listeners;

import static org.exoplatform.leadcapture.Utils.FIELDS_DELIMITER;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.exoplatform.leadcapture.services.ResourcesManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class NewResponseListener extends Listener<LeadEntity, ResponseEntity> {

  private static final Log               LOG = ExoLogger.getLogger(NewResponseListener.class);

  private LCMailService                  lcMailService;

  private MailTemplatesManagementService mailTemplatesManagementService;

  private ResourcesManagementService     resourcesManagementService;

  private FieldDAO                       fieldDAO;

  private LeadCaptureSettingsService     leadCaptureSettingsService;

  public NewResponseListener(LCMailService lcMailService,
                             MailTemplatesManagementService mailTemplatesManagementService,
                             ResourcesManagementService resourcesManagementService,
                             LeadCaptureSettingsService leadCaptureSettingsService,
                             FieldDAO fieldDAO) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
    this.resourcesManagementService = resourcesManagementService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
    this.fieldDAO = fieldDAO;
  }

  @Override
  public void onEvent(Event<LeadEntity, ResponseEntity> event) throws Exception {
    LeadEntity lead = event.getSource();
    String field = null;
    ResponseEntity responseEntity = event.getData();
    List<MailTemplateEntity> templates = mailTemplatesManagementService.getTemplatesbyEvent("newResponse");
    for (MailTemplateEntity template : templates) {
      MailContentDTO content = null;
      MailTemplateDTO mailTemplateDTO = mailTemplatesManagementService.toMailTemplateDTO(template);
      if (mailTemplateDTO.getContents().size() > 0) {
        content = Utils.getContentForMail(mailTemplateDTO, lead);
        if (content != null) {
          field = getField(template, responseEntity);
          if (field != null) {
            if (!field.equals("")) {
              if (isResourceRequest(field)) {
                field = resourcesManagementService.getResourceUrlByPath(field);
              }
            }
            lcMailService.sendMail(content.getContent(), content.getSubject(), lead, field);
            LOG.info("service=lead-capture operation=send_mail_to_lead parameters=\"lead_id:{},mail_template_id:{},mail_template_name:{},reason: NewLead\"",
                     lead.getId(),
                     mailTemplateDTO.getId(),
                     mailTemplateDTO.getName());
          }
        }
      }

    }
  }

  private String getField(MailTemplateEntity template, ResponseEntity responseEntity) {
    if (StringUtils.isEmpty(template.getForm()))
      return "";
    if (StringUtils.isEmpty(template.getField()) && responseEntity.getFormEntity().getName().equals(template.getForm()))
      return "";
    if (responseEntity.getFormEntity().getName().equals(template.getForm())) {
      List<FieldEntity> fields = fieldDAO.getFieldsByResponse(responseEntity.getId());
      for (FieldEntity fieldEntity : fields) {
        if (fieldEntity.getValue().contains(template.getField()) || template.getField().contains(fieldEntity.getValue()))
          return fieldEntity.getValue();
      }
    }
    return null;
  }

  private boolean isResourceRequest(String field) {
    String identifiers = leadCaptureSettingsService.getSettings().getResourcesIdentifier();
    if (StringUtils.isNotEmpty(identifiers)) {
      for (String identifier : identifiers.split(FIELDS_DELIMITER)) {
        if (field.contains(identifier)) {
          return true;
        }
      }
    }

    return false;
  }
}
