package org.exoplatform.leadcapture.listeners;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.MailTemplatesManagement;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class NewResponseListener extends Listener<LeadEntity, ResponseEntity> {

  private static final Log        LOG = ExoLogger.getLogger(NewResponseListener.class);

  private LCMailService           lcMailService;

  private MailTemplatesManagement mailTemplatesManagement;

  private FieldDAO fieldDAO;

  public NewResponseListener(LCMailService lcMailService, MailTemplatesManagement mailTemplatesManagement, FieldDAO fieldDAO) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagement = mailTemplatesManagement;
    this.fieldDAO = fieldDAO;
  }

  @Override
  public void onEvent(Event<LeadEntity, ResponseEntity> event) throws Exception {
    LeadEntity lead = event.getSource();
    ResponseEntity responseEntity = event.getData();
    List<MailTemplateEntity> templates = mailTemplatesManagement.getTemplatesbyEvent("newResponse");
    for (MailTemplateEntity template : templates) {
      MailContentDTO content = null;
      MailTemplateDTO mailTemplateDTO = mailTemplatesManagement.toMailTemplateDTO(template);
      if (mailTemplateDTO.getContents().size() > 0) {
        content = Utils.getContentForMail(mailTemplateDTO, lead);
        if (content != null && shouldBeSend(template, responseEntity)) {
          lcMailService.sendMail(content.getContent(), content.getSubject(), lead);
          LOG.info("Mail sent to {}",lead.getMail());
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
