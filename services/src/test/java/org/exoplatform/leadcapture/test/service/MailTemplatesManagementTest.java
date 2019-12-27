package org.exoplatform.leadcapture.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.MailTemplatesManagement;
import org.exoplatform.leadcapture.test.BaseTemplatesManagementTest;

public class MailTemplatesManagementTest extends BaseTemplatesManagementTest {

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    assertNotNull(mailTemplatesManagement);
  }

  @Test
  public void getTemplates() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    List<MailTemplateDTO> templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 0);
    newTemplate();
    templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 1);
  }

  @Test
  public void addTemplate() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    List<MailTemplateDTO> templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateDTO mailTemplateDTO = newTemplateDTO();
    MailContentDTO contentDTO = newContentDTO(mailTemplateDTO);
    List<MailContentDTO> contents = new ArrayList<>();
    contents.add(contentDTO);
    mailTemplateDTO.setContents(contents);
    mailTemplatesManagement.addTemplate(mailTemplateDTO);
    templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 1);
  }

  @Test
  public void getTemplatebyId() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    List<MailTemplateDTO> templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateEntity templateEntity = newTemplate();
    templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 1);
    assertNotNull(mailTemplatesManagement.getTemplatebyId(templateEntity.getId()));
  }

  @Test
  public void getTemplatesbyEvent() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    List<MailTemplateDTO> templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateEntity templateEntity = newTemplate();
    templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 1);
    assertNotNull(mailTemplatesManagement.getTemplatesbyEvent(templateEntity.getEvent()));
  }

  @Test
  public void deleteTemplate() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    List<MailTemplateDTO> templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 0);
    MailTemplateEntity templateEntity = newTemplate();
    templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 1);
    assertNotNull(mailTemplatesManagement.getTemplatebyId(templateEntity.getId()));
    mailTemplatesManagement.deleteTemplate(templateEntity);
    templates = mailTemplatesManagement.getTemplates();
    assertEquals(templates.size(), 0);
    assertEquals(mailTemplatesManagement.getTemplatebyId(templateEntity.getId()), null);
  }

  @Test
  public void updateTemplate() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    MailTemplateEntity templateEntity = newTemplate();
    assertEquals(templateEntity.getEvent(), templateEvent);
    templateEntity.setEvent("newTemplateEvent");
    mailTemplatesManagement.updateTemplate(mailTemplatesManagement.toMailTemplateDTO(templateEntity));
    assertEquals(templateEntity.getEvent(), "newTemplateEvent");
  }

  @Test
  public void toMailContentEntitiesList() {

  }

  @Test
  public void toMailTemplateDTO() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    MailTemplateEntity templateEntity = newTemplate();
    MailTemplateDTO mailTemplateDTO = mailTemplatesManagement.toMailTemplateDTO(templateEntity);
    compareMailTemplateResult(templateEntity, mailTemplateDTO);
  }

  @Test
  public void toMailTemplateEntity() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    MailTemplateDTO mailTemplateDTO = newTemplateDTO();
    MailTemplateEntity templateEntity = mailTemplatesManagement.toMailTemplateEntity(mailTemplateDTO);
    compareMailTemplateResult(templateEntity, mailTemplateDTO);
  }

  @Test
  public void toMailContentEntity() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    MailTemplateDTO mailTemplateDTO = newTemplateDTO();
    MailContentDTO mailContentDTO = newContentDTO(mailTemplateDTO);
    MailContentEntity mailContentEntity = mailTemplatesManagement.toMailContentEntity(mailContentDTO);
    compareMailContentResult(mailContentEntity, mailContentDTO);
  }

  @Test
  public void toMailContentDTO() {
    MailTemplatesManagement mailTemplatesManagement = getService(MailTemplatesManagement.class);
    MailTemplateEntity mailTemplateEntity = newTemplate();
    MailContentEntity mailContentEntity = newContent(mailTemplateEntity);
    MailContentDTO mailContentDTO = mailTemplatesManagement.toMailContentDTO(mailContentEntity);
    compareMailContentResult(mailContentEntity, mailContentDTO);
  }

}
