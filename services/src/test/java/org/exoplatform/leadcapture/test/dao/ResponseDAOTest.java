package org.exoplatform.leadcapture.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import org.exoplatform.leadcapture.dao.ResponseDAO;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

public class ResponseDAOTest extends BaseLeadManagementTest {

  protected String userName = "root";

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    ResponseDAO responseDAO = getService(ResponseDAO.class);
    assertNotNull(responseDAO);
  }

  @Test
  public void getResponsesByForm() {
    ResponseDAO responseDAO = getService(ResponseDAO.class);
    LeadEntity leadEntity = newLead();
    FormEntity formEntity = newForm();
    List<ResponseEntity> entities = responseDAO.getResponsesByForm(formEntity.getId(), leadEntity.getId());
    assertEquals(0, entities.size());
    newResponse(formEntity, leadEntity);
    entities = responseDAO.getResponsesByForm(formEntity.getId(), leadEntity.getId());
    assertNotNull(entities);
    assertEquals(1, entities.size());
  }

  @Test
  public void getResponsesByLead() {
    ResponseDAO responseDAO = getService(ResponseDAO.class);
    LeadEntity leadEntity = newLead();
    FormEntity formEntity = newForm();
    List<ResponseEntity> entities = responseDAO.getResponsesByForm(formEntity.getId(), leadEntity.getId());
    assertEquals(0, entities.size());
    newResponse(formEntity, leadEntity);
    entities = responseDAO.getResponsesByLead(leadEntity.getId());
    assertNotNull(entities);
    assertEquals(1, entities.size());
  }

}
