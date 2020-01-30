package org.exoplatform.leadcapture.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.junit.Test;

import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.test.BaseTemplatesManagementTest;

public class MailTemplatesManagementServiceTest extends BaseTemplatesManagementTest {

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    assertNotNull(mailTemplatesManagementService);
  }

  @Test
  public void getTemplates() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    List<MailTemplateDTO> templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 0);
    newTemplate();
    templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 1);
  }

  @Test
  public void addTemplate() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    List<MailTemplateDTO> templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateDTO mailTemplateDTO = newTemplateDTO();
    MailContentDTO contentDTO = newContentDTO(mailTemplateDTO);
    List<MailContentDTO> contents = new ArrayList<>();
    contents.add(contentDTO);
    mailTemplateDTO.setContents(contents);
    mailTemplatesManagementService.addTemplate(mailTemplateDTO);
    templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 1);
  }

  @Test
  public void getTemplatebyId() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    List<MailTemplateDTO> templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateEntity templateEntity = newTemplate();
    templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 1);
    assertNotNull(mailTemplatesManagementService.getTemplatebyId(templateEntity.getId()));
  }

  @Test
  public void getTemplatesbyEvent() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    List<MailTemplateDTO> templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateEntity templateEntity = newTemplate();
    templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 1);
    assertNotNull(mailTemplatesManagementService.getTemplatesbyEvent(templateEntity.getEvent()));
  }

  @Test
  public void deleteTemplate() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    List<MailTemplateDTO> templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateEntity templateEntity = newTemplate();
    templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 1);
    assertNotNull(mailTemplatesManagementService.getTemplatebyId(templateEntity.getId()));
    mailTemplatesManagementService.deleteTemplate(templateEntity);
    templates = mailTemplatesManagementService.getTemplates();
    assertEquals(templates.size(), 0);
    assertEquals(mailTemplatesManagementService.getTemplatebyId(templateEntity.getId()), null);
  }

  @Test
  public void updateTemplate() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    MailTemplateEntity templateEntity = newTemplate();
    assertEquals(templateEntity.getEvent(), templateEvent);
    templateEntity.setEvent("newTemplateEvent");
    mailTemplatesManagementService.updateTemplate(mailTemplatesManagementService.toMailTemplateDTO(templateEntity));
    assertEquals(templateEntity.getEvent(), "newTemplateEvent");
  }

  @Test
  public void toMailContentEntitiesList() {

  }

  @Test
  public void toMailTemplateDTO() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    MailTemplateEntity templateEntity = newTemplate();
    MailTemplateDTO mailTemplateDTO = mailTemplatesManagementService.toMailTemplateDTO(templateEntity);
    compareMailTemplateResult(templateEntity, mailTemplateDTO);
  }

  @Test
  public void toMailTemplateEntity() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    MailTemplateDTO mailTemplateDTO = newTemplateDTO();
    MailTemplateEntity templateEntity = mailTemplatesManagementService.toMailTemplateEntity(mailTemplateDTO);
    compareMailTemplateResult(templateEntity, mailTemplateDTO);
  }

  @Test
  public void toMailContentEntity() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    MailTemplateDTO mailTemplateDTO = newTemplateDTO();
    MailContentDTO mailContentDTO = newContentDTO(mailTemplateDTO);
    MailContentEntity mailContentEntity = mailTemplatesManagementService.toMailContentEntity(mailContentDTO);
    compareMailContentResult(mailContentEntity, mailContentDTO);
  }

  @Test
  public void toMailContentDTO() {
    MailTemplatesManagementService mailTemplatesManagementService = getService(MailTemplatesManagementService.class);
    MailTemplateEntity mailTemplateEntity = newTemplate();
    MailContentEntity mailContentEntity = newContent(mailTemplateEntity);
    MailContentDTO mailContentDTO = mailTemplatesManagementService.toMailContentDTO(mailContentEntity);
    compareMailContentResult(mailContentEntity, mailContentDTO);
  }

}
