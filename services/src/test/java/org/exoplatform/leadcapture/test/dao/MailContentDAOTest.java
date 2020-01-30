package org.exoplatform.leadcapture.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.exoplatform.leadcapture.dao.MailContentDAO;
import org.exoplatform.leadcapture.dao.MailTemplateDAO;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.test.BaseTemplatesManagementTest;
import org.junit.Test;

import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

import java.util.List;

public class MailContentDAOTest extends BaseTemplatesManagementTest {

  protected String userName = "root";

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    MailContentDAO mailContentDAO = getService(MailContentDAO.class);
    assertNotNull(mailContentDAO);
  }

  @Test
  public void getContentByTemplate() {
    MailContentDAO mailContentDAO = getService(MailContentDAO.class);
    MailTemplateEntity mailTemplateEntity = newTemplate();
    List<MailContentEntity> entities = mailContentDAO.getContentByTemplate(mailTemplateEntity.getId());
    assertNotNull(entities);
    assertEquals(0, entities.size());
    newContent(mailTemplateEntity);
    entities = mailContentDAO.getContentByTemplate(mailTemplateEntity.getId());
    assertNotNull(entities);
    assertEquals(1, entities.size());
  }

}
