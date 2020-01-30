package org.exoplatform.leadcapture.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

public class LeadDAOTest extends BaseLeadManagementTest {

  protected String userName = "root";

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    LeadDAO leadDAO = getService(LeadDAO.class);
    assertNotNull(leadDAO);
  }

  @Test
  public void getLeadByMail() {
    LeadDAO leadDAO = getService(LeadDAO.class);
    LeadEntity entity = leadDAO.getLeadByMail(mail);
    assertEquals(null, entity);
    newLead();
    entity = leadDAO.getLeadByMail(mail);
    assertNotNull(entity);
  }

}
