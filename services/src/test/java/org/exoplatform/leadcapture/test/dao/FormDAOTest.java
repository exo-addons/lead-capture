package org.exoplatform.leadcapture.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

public class FormDAOTest extends BaseLeadManagementTest {

  protected String userName = "root";

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    FormDAO formDAO = getService(FormDAO.class);
    assertNotNull(formDAO);
  }

  @Test
  public void getFormByName() {
    FormDAO formDAO = getService(FormDAO.class);
    FormEntity entity = formDAO.getFormByName(formName);
    assertEquals(null, entity);
    newForm();
    entity = formDAO.getFormByName(formName);
    assertNotNull(entity);
  }
}
