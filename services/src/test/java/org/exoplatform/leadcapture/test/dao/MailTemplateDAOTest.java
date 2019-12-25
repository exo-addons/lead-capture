package org.exoplatform.leadcapture.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.exoplatform.leadcapture.dao.MailTemplateDAO;
import org.exoplatform.leadcapture.dao.ResponseDAO;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.test.BaseTemplatesManagementTest;
import org.junit.Test;

import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

import java.util.List;

public class MailTemplateDAOTest extends BaseTemplatesManagementTest {

  protected String userName = "root";

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    MailTemplateDAO mailTemplateDAO = getService(MailTemplateDAO.class);
    assertNotNull(mailTemplateDAO);
  }

  @Test
  public void getTemplatesbyEvent() {
    MailTemplateDAO mailTemplateDAO = getService(MailTemplateDAO.class);
    List<MailTemplateEntity> entities = mailTemplateDAO.getTemplatesbyEvent(templateEvent);
    assertNotNull(entities);
    assertEquals(0, entities.size());
    newTemplate();
    entities = mailTemplateDAO.getTemplatesbyEvent(templateEvent);
    assertNotNull(entities);
    assertEquals(1, entities.size());
  }

}
