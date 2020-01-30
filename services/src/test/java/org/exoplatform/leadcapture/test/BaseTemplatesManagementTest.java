package org.exoplatform.leadcapture.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.leadcapture.dao.MailContentDAO;
import org.exoplatform.leadcapture.dao.MailTemplateDAO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;

public abstract class BaseTemplatesManagementTest {

  protected static PortalContainer container;

  protected String                 templateName        = "templateName";

  protected String                 templateDescription = "templateDescription";

  protected String                 templateEvent       = "templateEvent";

  protected String                 templateForm        = "templateForm";

  protected String                 templateField       = "templateField";

  protected String                 mailSubject         = "mailSubject";

  protected String                 mailLanguage        = "en";

  protected String                 mailContent         = "mailContent";

  @BeforeClass
  public static void beforeTest() {
    container = PortalContainer.getInstance();
    assertNotNull(container);
    assertTrue(container.isStarted());
  }

  @Before
  public void beforeMethodTest() {
    RequestLifeCycle.begin(container);
  }

  @After
  public void afterMethodTest() {
    MailTemplateDAO mailTemplateDAO = getService(MailTemplateDAO.class);
    MailContentDAO mailContentDAO = getService(MailContentDAO.class);

    RequestLifeCycle.end();
    RequestLifeCycle.begin(container);
    mailContentDAO.deleteAll();
    mailTemplateDAO.deleteAll();
    RequestLifeCycle.end();
  }

  protected <T> T getService(Class<T> componentType) {
    return container.getComponentInstanceOfType(componentType);
  }

  protected MailTemplateEntity newTemplate() {
    MailTemplateDAO mailTemplateDAO = getService(MailTemplateDAO.class);
    MailTemplateEntity mailTemplateEntity = new MailTemplateEntity();
    mailTemplateEntity.setName(templateName);
    mailTemplateEntity.setDescription(templateDescription);
    mailTemplateEntity.setEvent(templateEvent);
    mailTemplateEntity.setForm(templateForm);
    mailTemplateEntity.setField(templateField);
    mailTemplateEntity = mailTemplateDAO.create(mailTemplateEntity);
    return mailTemplateEntity;
  }

  protected MailTemplateDTO newTemplateDTO() {
    MailTemplateDTO mailTemplateDTO = new MailTemplateDTO();
    mailTemplateDTO.setName(templateName);
    mailTemplateDTO.setDescription(templateDescription);
    mailTemplateDTO.setEvent(templateEvent);
    mailTemplateDTO.setForm(templateForm);
    mailTemplateDTO.setField(templateField);
    return mailTemplateDTO;
  }

  protected MailContentEntity newContent(MailTemplateEntity mailTemplateEntity) {
    MailContentDAO mailContentDAO = getService(MailContentDAO.class);
    MailContentEntity mailContentEntity = new MailContentEntity();
    mailContentEntity.setSubject(mailSubject);
    mailContentEntity.setLanguage(mailLanguage);
    mailContentEntity.setContent(mailContent);
    mailContentEntity.setMailTemplateEntity(mailTemplateEntity);
    mailContentEntity = mailContentDAO.create(mailContentEntity);
    return mailContentEntity;
  }

  protected MailContentDTO newContentDTO(MailTemplateDTO mailTemplateDTO) {
    MailContentDTO mailContentDTO = new MailContentDTO();
    mailContentDTO.setSubject(mailSubject);
    mailContentDTO.setLanguage(mailLanguage);
    mailContentDTO.setContent(mailContent);
    mailContentDTO.setMailTemplateDTO(mailTemplateDTO);
    return mailContentDTO;
  }

  protected void compareMailTemplateResult(MailTemplateEntity mailTemplateEntity, MailTemplateDTO mailTemplateDTO) {
    assertEquals(mailTemplateEntity.getId(), mailTemplateDTO.getId());
    assertEquals(mailTemplateEntity.getDescription(), mailTemplateDTO.getDescription());
    assertEquals(mailTemplateEntity.getEvent(), mailTemplateDTO.getEvent());
    assertEquals(mailTemplateEntity.getField(), mailTemplateDTO.getField());
    assertEquals(mailTemplateEntity.getForm(), mailTemplateDTO.getForm());
  }

  protected void compareMailContentResult(MailContentEntity mailContentEntity, MailContentDTO mailContentDTO) {
    assertEquals(mailContentEntity.getId(), mailContentDTO.getId());
    assertEquals(mailContentEntity.getContent(), mailContentDTO.getContent());
    assertEquals(mailContentEntity.getLanguage(), mailContentDTO.getLanguage());
    assertEquals(mailContentEntity.getSubject(), mailContentDTO.getSubject());
    compareMailTemplateResult(mailContentEntity.getMailTemplateEntity(), mailContentDTO.getMailTemplateDTO());
  }
}
