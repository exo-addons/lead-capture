package org.exoplatform.leadcapture.services;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.leadcapture.dao.CompaignDAO;
import org.exoplatform.leadcapture.dto.CompaignDTO;
import org.exoplatform.leadcapture.entity.CompaignEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class CompaignManagementService {

  private final Log   LOG = ExoLogger.getLogger(CompaignManagementService.class);

  private CompaignDAO compaignDAO;
  private MailTemplatesManagementService mailTemplatesManagementService;

  public CompaignManagementService(CompaignDAO compaignDAO, MailTemplatesManagementService mailTemplatesManagementService) {
    this.compaignDAO = compaignDAO;
    this.mailTemplatesManagementService = mailTemplatesManagementService;
  }

  public List<CompaignDTO> getCompaigns() {
    List<CompaignDTO> compaignDTOList = new ArrayList<>();
    List<CompaignEntity> compaignEntities = compaignDAO.findAll();
    if (compaignEntities != null) {
      for (CompaignEntity compaignEntity : compaignEntities) {
        if (compaignEntity != null) {
          compaignDTOList.add(toCompaignDTO(compaignEntity));
        }
      }
    }
    return compaignDTOList;
  }

  public CompaignDTO addCompaign(CompaignDTO compaignDTO) {
    return toCompaignDTO(compaignDAO.create(toCompaignEntity(compaignDTO)));
  }

  public void deleteCompaign(CompaignEntity compaignEntityEntity) {
    compaignDAO.delete(compaignEntityEntity);
  }

  public CompaignDTO updateCompaign(CompaignDTO compaignDTO) {
    CompaignEntity newRessource = compaignDAO.update(toCompaignEntity(compaignDTO));
    return toCompaignDTO(newRessource);

  }

  public CompaignEntity getCompaignById(Long id) {
    return compaignDAO.find(id);

  }


  public List<CompaignDTO> toCompaignDtoList(List<CompaignEntity> compaignEntities) {
    List<CompaignDTO> compaignDTOS = new ArrayList<>();
    for (CompaignEntity compaignEntity : compaignEntities) {
      compaignDTOS.add(toCompaignDTO(compaignEntity));
    }
    return compaignDTOS;
  }

  public CompaignDTO toCompaignDTO(CompaignEntity compaignEntity) {
    CompaignDTO compaignDTO = new CompaignDTO();
    compaignDTO.setId(compaignEntity.getId());
    compaignDTO.setName(compaignEntity.getName());
    compaignDTO.setValue(compaignEntity.getValue());
    compaignDTO.setField(compaignEntity.getField());
    compaignDTO.setEnabled(compaignEntity.getEnabled());
    compaignDTO.setAfter(compaignEntity.getAfter());
    if (compaignEntity.getMailTemplateEntity() != null) {
      compaignDTO.setMailTemplateDTO(mailTemplatesManagementService.toMailTemplateDTO(compaignEntity.getMailTemplateEntity() ));
    }
    return compaignDTO;
  }

  public CompaignEntity toCompaignEntity(CompaignDTO compaignDTO) {
    CompaignEntity compaignEntity = new CompaignEntity();
    compaignEntity.setId(compaignDTO.getId());
    compaignEntity.setName(compaignDTO.getName());
    compaignEntity.setField(compaignDTO.getField());
    compaignEntity.setValue(compaignDTO.getValue());
    compaignEntity.setEnabled(compaignDTO.getEnabled());
    compaignEntity.setAfter(compaignDTO.getAfter());
    if (compaignDTO.getMailTemplateDTO() != null) {
      compaignEntity.setMailTemplateEntity(mailTemplatesManagementService.toMailTemplateEntity(compaignDTO.getMailTemplateDTO() ));
    }
    return compaignEntity;
  }
}
