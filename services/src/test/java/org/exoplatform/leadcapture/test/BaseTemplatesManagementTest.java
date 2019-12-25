package org.exoplatform.leadcapture.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.leadcapture.dao.MailContentDAO;
import org.exoplatform.leadcapture.dao.MailTemplateDAO;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;

public abstract class BaseTemplatesManagementTest {

  protected static PortalContainer   container;

  protected String                   templateName                = "templateName";

  protected String                   templateDescription         = "templateDescription";

  protected String                   templateEvent               = "templateEvent";

  protected String                   templateForm                = "templateForm";

  protected String                   templateField               = "templateField";

  protected String                   mailSubject                 = "mailSubject";

  protected String                   mailLanguage                = "en";

  protected String                   mailContent                 = "mailContent";

  protected List<MailContentEntity>  mailContentEntitiesToClean  = new ArrayList<>();

  protected List<MailTemplateEntity> mailTemplateEntitiesToClean = new ArrayList<>();

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

    if (!mailContentEntitiesToClean.isEmpty()) {
      for (MailContentEntity entity : mailContentEntitiesToClean) {
        mailContentDAO.delete(entity);
      }
    }
    if (!mailTemplateEntitiesToClean.isEmpty()) {
      for (MailTemplateEntity entity : mailTemplateEntitiesToClean) {
        mailTemplateDAO.delete(entity);
      }
    }
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
    mailTemplateEntitiesToClean.add(mailTemplateEntity);
    return mailTemplateEntity;
  }

  protected MailContentEntity newContent(MailTemplateEntity mailTemplateEntity) {
    MailContentDAO mailContentDAO = getService(MailContentDAO.class);
    MailContentEntity mailContentEntity = new MailContentEntity();
    mailContentEntity.setSubject(mailSubject);
    mailContentEntity.setLanguage(mailLanguage);
    mailContentEntity.setContent(mailContent);
    mailContentEntity.setMailTemplateEntity(mailTemplateEntity);
    mailContentEntity = mailContentDAO.create(mailContentEntity);
    mailContentEntitiesToClean.add(mailContentEntity);
    return mailContentEntity;
  }

}
