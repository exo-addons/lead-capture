package org.exoplatform.leadcapture.test.service;

import org.exoplatform.leadcapture.dto.*;
import org.exoplatform.leadcapture.entity.*;
import org.json.JSONException;
import org.junit.Test;

import org.exoplatform.leadcapture.services.LeadsManagement;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class LeadsManagementTest extends BaseLeadManagementTest {

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    assertNotNull(leadsManagement);
  }

  @Test
  public void addLeadInfo() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    FormInfo formInfo= new FormInfo();
    formInfo.setLead(newLeadDto());
    formInfo.setResponse(newResponseDTO());
    List<LeadDTO> leads = leadsManagement.getLeads();
    assertNotNull(leads);
    assertEquals(0, leads.size());
      leadsManagement.addLeadInfo(formInfo,false);
      leads = leadsManagement.getLeads();
      assertNotNull(leads);
      assertEquals(1, leads.size());
      assertEquals(1,leadsManagement.getResponses(leads.get(0).getId()).length());

  }

  @Test
  public void deleteLead() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertNotNull(leadsManagement.getLeadbyId(leadEntity.getId()));

      leadsManagement.deleteLead(leadEntity);
      assertEquals(null, leadsManagement.getLeadbyId(leadEntity.getId()));

  }

  @Test
  public void updateLead() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertEquals(language,leadEntity.getLanguage());
    leadEntity.setLanguage("fr");

      leadsManagement.updateLead(leadsManagement.toLeadDto(leadEntity));
      leadEntity = leadsManagement.getLeadbyId(leadEntity.getId());
      assertEquals("fr", leadEntity.getLanguage());

  }

  @Test
  public void updateForm() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    FormEntity formEntity = newForm();
    assertEquals(formName,formEntity.getName());
    formEntity.setName("new name");

    formEntity = leadsManagement.updateForm(formEntity);
    assertEquals("new name",formEntity.getName());

  }

  @Test
  public void assigneLead() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertEquals(assignee,leadEntity.getAssignee());
      leadsManagement.assigneLead(leadEntity.getId(),"testUser");
      leadEntity = leadsManagement.getLeadbyId(leadEntity.getId());
      assertEquals("testUser", leadEntity.getAssignee());
  }

  @Test
  public void updateStatus() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertEquals(status,leadEntity.getStatus());
    leadsManagement.updateStatus(leadEntity.getId(),"Accepted");
    leadEntity = leadsManagement.getLeadbyId(leadEntity.getId());
    assertEquals("Accepted", leadEntity.getStatus());
  }

  @Test
  public void getLeads() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    assertEquals(0, leadsManagement.getLeads().size());
    newLead();
    assertEquals(1, leadsManagement.getLeads().size());
  }

  @Test
  public void getResponses() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertEquals(0, leadsManagement.getResponses(leadEntity.getId()).length());
    newResponse(newForm(),leadEntity);
    assertEquals(1, leadsManagement.getResponses(leadEntity.getId()).length());
  }

  @Test
  public void addResponse() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertEquals(0, leadsManagement.getResponses(leadEntity.getId()).length());
    leadsManagement.addResponse(newResponseDTO(),leadEntity);
    assertEquals(1, leadsManagement.getResponses(leadEntity.getId()).length());
  }

  @Test
  public void getLeadbyId() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertNotNull(leadsManagement.getLeadbyId(leadEntity.getId()));
  }

  @Test
  public void getLeadByMail() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    assertNotNull(leadsManagement.getLeadByMail(mail));
  }

  @Test
  public void mergeLead() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    assertNotNull(leadsManagement.mergeLead(newLead(),newLeadDto()));
  }

  @Test
  public void toFormJson() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    try {
      assertNotNull(leadsManagement.toFormJson(newForm()));
    } catch (JSONException e) {
      fail();
    }
  }

  @Test
  public void toLeadDto() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadEntity leadEntity = newLead();
    LeadDTO leadDTO = leadsManagement.toLeadDto(leadEntity);
    compareLeadResult(leadEntity,leadDTO);
  }

  @Test
  public void toLeadEntity() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    LeadDTO leadDTO = newLeadDto();
    LeadEntity leadEntity = leadsManagement.toLeadEntity(leadDTO);
    compareLeadResult(leadEntity,leadDTO);
  }

  @Test
  public void toFieldDto() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    ResponseEntity responseEntity = newResponse(newForm(),newLead());
    FieldEntity fieldEntity = newField(responseEntity);
    FieldDTO fieldDTO = leadsManagement.toFieldDto(fieldEntity);
    compareFieldResult(fieldEntity,fieldDTO);
  }

  @Test
  public void toFieldEntity() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    FieldDTO fieldDTO = newFieldDTO();
    FieldEntity fieldEntity = leadsManagement.toFieldEntity(fieldDTO);
    compareFieldResult(fieldEntity,fieldDTO);
  }

  @Test
  public void toFormDto() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    FormEntity formEntity = newForm();
    FormDTO formDTO = leadsManagement.toFormDto(formEntity);
    compareFormResult(formEntity,formDTO);
  }

  @Test
  public void toFormEntity() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    FormDTO formDTO = newFormDto();
    FormEntity formEntity = leadsManagement.toFormEntity(formDTO);
    compareFormResult(formEntity,formDTO);
  }

  @Test
  public void toResponseDto() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    ResponseEntity responseEntity = newResponse(newForm(),newLead());
    ResponseDTO responseDTO = leadsManagement.toResponseDto(responseEntity);
    compareResponseResult(responseEntity,responseDTO);

  }

  @Test
  public void toResponseEntity() {
    LeadsManagement leadsManagement = getService(LeadsManagement.class);
    ResponseDTO responseDTO = newResponseDTO();
    ResponseEntity responseEntity = leadsManagement.toResponseEntity(responseDTO);
    compareResponseResult(responseEntity,responseDTO);
  }

}
