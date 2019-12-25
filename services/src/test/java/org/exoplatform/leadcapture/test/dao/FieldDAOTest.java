package org.exoplatform.leadcapture.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

public class FieldDAOTest extends BaseLeadManagementTest {

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
    FieldDAO fieldDAO = getService(FieldDAO.class);
    LeadEntity leadEntity = newLead();
    FormEntity formEntity = newForm();
    ResponseEntity responseEntity = newResponse(formEntity, leadEntity);
    List<FieldEntity> entities = fieldDAO.getFileldsByResponse(responseEntity.getId());
    assertEquals(0, entities.size());
    newField(responseEntity);
    entities = fieldDAO.getFileldsByResponse(responseEntity.getId());
    assertNotNull(entities);
    assertEquals(1, entities.size());
  }

}
