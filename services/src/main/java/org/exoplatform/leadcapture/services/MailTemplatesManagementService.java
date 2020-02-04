package org.exoplatform.leadcapture.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.leadcapture.dao.MailContentDAO;
import org.exoplatform.leadcapture.dao.MailTemplateDAO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class MailTemplatesManagementService {

  private final Log       LOG = ExoLogger.getLogger(MailTemplatesManagementService.class);

  private MailTemplateDAO mailTemplateDAO;

  private MailContentDAO  mailContentDAO;

  public MailTemplatesManagementService(MailTemplateDAO mailTemplateDAO, MailContentDAO mailContentDAO) {
    this.mailTemplateDAO = mailTemplateDAO;
    this.mailContentDAO = mailContentDAO;
  }

  public List<MailTemplateDTO> getTemplates() {
    List<MailTemplateDTO> mailTemplateList = new ArrayList<>();
    List<MailTemplateEntity> mailTemplateEntities = mailTemplateDAO.findAll();
    if (mailTemplateEntities != null) {
      for (MailTemplateEntity mailTemplateEntity : mailTemplateEntities) {
        if (mailTemplateEntity != null) {
          mailTemplateList.add(toMailTemplateDTO(mailTemplateEntity));
        }
      }
    }
    return mailTemplateList;
  }

  public MailTemplateDTO addTemplate(MailTemplateDTO templateDTO) {

    List<MailContentDTO> contents = templateDTO.getContents();
    List<MailContentEntity> contentsEntities = new ArrayList<>();
    templateDTO.setContents(new ArrayList<>());
    MailTemplateEntity newTemplate = mailTemplateDAO.create(toMailTemplateEntity(templateDTO));
    for (MailContentDTO contentDTO : contents) {
      if(StringUtils.isNoneEmpty(contentDTO.getContent()) && StringUtils.isNoneEmpty(contentDTO.getSubject())){
        MailContentEntity mailContentEntity = toMailContentEntity(contentDTO);
        mailContentEntity.setMailTemplateEntity(newTemplate);
        MailContentEntity mailContentEntity_ = mailContentDAO.create(mailContentEntity);
        contentsEntities.add(mailContentEntity_);
      }
    }
    return toMailTemplateDTO(newTemplate);
  }

  public MailTemplateEntity getTemplatebyId(Long id) {
    return mailTemplateDAO.find(id);

  }

  public List<MailTemplateEntity> getTemplatesbyEvent(String event) {
    return mailTemplateDAO.getTemplatesbyEvent(event);

  }

  public void deleteTemplate(MailTemplateEntity mailTemplateEntity) {
    List<MailContentDTO> contents = new ArrayList<>();
    mailContentDAO.deleteAll(mailContentDAO.getContentByTemplate(mailTemplateEntity.getId()));
    mailTemplateDAO.delete(mailTemplateEntity);
  }

  public MailTemplateDTO updateTemplate(MailTemplateDTO templateDTO) {
    mailContentDAO.updateAll(toMailContentEntitiesList(templateDTO.getContents()));
    MailTemplateEntity newTemplate = mailTemplateDAO.update(toMailTemplateEntity(templateDTO));
    return toMailTemplateDTO(newTemplate);

  }

  public List<MailContentEntity> toMailContentEntitiesList(List<MailContentDTO> contents) {
    List<MailContentEntity> mailContentEntityList = new ArrayList<>();
    for (MailContentDTO mailContentDTO : contents) {
      mailContentEntityList.add(toMailContentEntity(mailContentDTO));
    }
    return mailContentEntityList;
  }

  public MailTemplateDTO toMailTemplateDTO(MailTemplateEntity mailTemplateEntity) {
    MailTemplateDTO mailTemplateDTO = new MailTemplateDTO();
    mailTemplateDTO.setId(mailTemplateEntity.getId());
    mailTemplateDTO.setName(mailTemplateEntity.getName());
    mailTemplateDTO.setDescription(mailTemplateEntity.getDescription());
    mailTemplateDTO.setEvent(mailTemplateEntity.getEvent());
    mailTemplateDTO.setField(mailTemplateEntity.getField());
    mailTemplateDTO.setForm(mailTemplateEntity.getForm());
    List<MailContentDTO> contents = new ArrayList<>();
    for (MailContentEntity content : mailContentDAO.getContentByTemplate(mailTemplateEntity.getId())) {
      if (content != null) {
        contents.add(toMailContentDTO(content));
      }
    }
    mailTemplateDTO.setContents(contents);
    return mailTemplateDTO;
  }

  public MailTemplateEntity toMailTemplateEntity(MailTemplateDTO mailTemplateDTO) {
    MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
    mailTemplateEntity.setId(mailTemplateDTO.getId());
    mailTemplateEntity.setName(mailTemplateDTO.getName());
    mailTemplateEntity.setDescription(mailTemplateDTO.getDescription());
    mailTemplateEntity.setEvent(mailTemplateDTO.getEvent());
    mailTemplateEntity.setField(mailTemplateDTO.getField());
    mailTemplateEntity.setForm(mailTemplateDTO.getForm());
    return mailTemplateEntity;
  }

  public MailContentEntity toMailContentEntity(MailContentDTO mailContentDTO) {
    MailContentEntity mailContentEntity = new MailContentEntity();
    mailContentEntity.setId(mailContentDTO.getId());
    mailContentEntity.setContent(mailContentDTO.getContent());
    mailContentEntity.setLanguage(mailContentDTO.getLanguage());
    mailContentEntity.setSubject(mailContentDTO.getSubject());
    if (mailContentDTO.getMailTemplateDTO() != null) {
      mailContentEntity.setMailTemplateEntity(toMailTemplateEntity(mailContentDTO.getMailTemplateDTO()));
    }
    return mailContentEntity;
  }

  public MailContentDTO toMailContentDTO(MailContentEntity mailContentEntity) {
    MailContentDTO mailContentDTO = new MailContentDTO();
    mailContentDTO.setId(mailContentEntity.getId());
    mailContentDTO.setContent(mailContentEntity.getContent());
    mailContentDTO.setLanguage(mailContentEntity.getLanguage());
    mailContentDTO.setSubject(mailContentEntity.getSubject());
    if (mailContentEntity.getMailTemplateEntity() != null) {
      MailTemplateDTO mailTemplateDTO = new MailTemplateDTO();
      mailTemplateDTO.setId(mailContentEntity.getMailTemplateEntity().getId());
      mailTemplateDTO.setEvent(mailContentEntity.getMailTemplateEntity().getEvent());
      mailTemplateDTO.setForm(mailContentEntity.getMailTemplateEntity().getForm());
      mailTemplateDTO.setField(mailContentEntity.getMailTemplateEntity().getField());
      mailTemplateDTO.setDescription(mailContentEntity.getMailTemplateEntity().getDescription());
      mailTemplateDTO.setName(mailContentEntity.getMailTemplateEntity().getForm());
      mailContentDTO.setMailTemplateDTO(mailTemplateDTO);
    }
    return mailContentDTO;
  }

}
