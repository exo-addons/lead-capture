package org.exoplatform.leadcapture.services;

import org.exoplatform.leadcapture.dao.*;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;

public class MailTemplatesManagement {

    private final Log LOG = ExoLogger.getLogger(MailTemplatesManagement.class);

    private MailTemplateDAO mailTemplateDAO;

    private MailContentDAO mailContentDAO;


    public MailTemplatesManagement(MailTemplateDAO mailTemplateDAO, MailContentDAO mailContentDAO) {
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


    public MailTemplateDTO addTemplate(MailTemplateDTO templateDTO){

        List<MailContentDTO> contents = (List<MailContentDTO>) templateDTO.getContents();
        List<MailContentEntity> contentsEntities = new ArrayList<>();
        templateDTO.setContents(new ArrayList<>());
        MailTemplateEntity newTemplate = mailTemplateDAO.create(toMailTemplateEntity(templateDTO));
        for (MailContentDTO contentDTO : contents){
            MailContentEntity mailContentEntity = toMailContentEntity(contentDTO);
            mailContentEntity.setMailTemplateEntity(newTemplate);
            MailContentEntity mailContentEntity_ = mailContentDAO.create(mailContentEntity);
            contentsEntities.add(mailContentEntity_);
        }
        return toMailTemplateDTO(newTemplate);
    }


    public MailTemplateEntity getTemplatebyId(Long id){
        return mailTemplateDAO.find(id);

    }


    public void deleteTemplate(MailTemplateEntity mailTemplateEntity){
        List<MailContentDTO> contents = new ArrayList<>();
        mailContentDAO.deleteAll(mailContentDAO.getContentByTemplate(mailTemplateEntity.getId()));
        mailTemplateDAO.delete(mailTemplateEntity);
    }


    public MailTemplateDTO updateTemplate(MailTemplateDTO templateDTO){
        mailContentDAO.updateAll(toMailContentEntitiesList(templateDTO.getContents()));
        MailTemplateEntity newTemplate = mailTemplateDAO.update(toMailTemplateEntity(templateDTO));
        return toMailTemplateDTO(newTemplate);

    }

    public List<MailContentEntity> toMailContentEntitiesList (List<MailContentDTO> contents){
        List<MailContentEntity> mailContentEntityList = new ArrayList<>();
        for(MailContentDTO mailContentDTO : contents){
            mailContentEntityList.add(toMailContentEntity(mailContentDTO));
        }
        return mailContentEntityList;
    }

    public  MailTemplateDTO toMailTemplateDTO(MailTemplateEntity mailTemplateEntity) {
        MailTemplateDTO mailTemplateDTO = new MailTemplateDTO();
        mailTemplateDTO.setId(mailTemplateEntity.getId());
        mailTemplateDTO.setName(mailTemplateEntity.getName());
        mailTemplateDTO.setDescription(mailTemplateEntity.getDescription());
       List<MailContentDTO> contents = new ArrayList<>();
        for (MailContentEntity content : mailContentDAO.getContentByTemplate(mailTemplateEntity.getId())) {
            if(content!=null){contents.add(toMailContentDTO(content));}
        }
        mailTemplateDTO.setContents(contents);
        return mailTemplateDTO;
    }


    public   MailTemplateEntity toMailTemplateEntity(MailTemplateDTO mailTemplateDTO) {
        MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
        mailTemplateEntity.setId(mailTemplateDTO.getId());
        mailTemplateEntity.setName(mailTemplateDTO.getName());
        mailTemplateEntity.setDescription(mailTemplateDTO.getDescription());
        return mailTemplateEntity;
    }
    public  MailContentEntity toMailContentEntity(MailContentDTO mailContentDTO) {
        MailContentEntity mailContentEntity = new MailContentEntity();
        mailContentEntity.setId(mailContentDTO.getId());
        mailContentEntity.setContent(mailContentDTO.getContent());
        mailContentEntity.setLanguage(mailContentDTO.getLanguage());
        mailContentEntity.setSubject(mailContentDTO.getSubject());
        if(mailContentDTO.getMailTemplateDTO()!=null){
            mailContentEntity.setMailTemplateEntity(toMailTemplateEntity(mailContentDTO.getMailTemplateDTO()));
        }
        return mailContentEntity;
    }

    public  MailContentDTO toMailContentDTO(MailContentEntity mailContentEntity) {
        MailContentDTO mailContentDTO = new MailContentDTO();
        mailContentDTO.setId(mailContentEntity.getId());
        mailContentDTO.setContent(mailContentEntity.getContent());
        mailContentDTO.setLanguage(mailContentEntity.getLanguage());
        mailContentDTO.setSubject(mailContentEntity.getSubject());
        if(mailContentEntity.getMailTemplateEntity()!=null){
            MailTemplateDTO mailTemplateDTO = new MailTemplateDTO();
            mailTemplateDTO.setId(mailContentEntity.getMailTemplateEntity().getId());
            mailContentDTO.setMailTemplateDTO(mailTemplateDTO);
        }
        return mailContentDTO;
    }

}
