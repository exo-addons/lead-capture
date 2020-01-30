package org.exoplatform.leadcapture.test.service;

import static org.junit.Assert.*;

import java.util.List;

import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.json.JSONException;
import org.junit.Test;

import org.exoplatform.leadcapture.dto.*;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.test.BaseLeadManagementTest;

public class LeadsManagementTest extends BaseLeadManagementTest {

  /**
   * Check that service is instantiated and functional
   */
  @Test
  public void testServiceInstantiated() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    assertNotNull(leadsManagementService);
  }

  @Test
  public void addLeadInfo() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    FormInfo formInfo = new FormInfo();
    formInfo.setLead(newLeadDto());
    formInfo.setResponse(newResponseDTO());
    List<LeadDTO> leads = leadsManagementService.getLeads();
    assertNotNull(leads);
    assertEquals(0, leads.size());
    leadsManagementService.addLeadInfo(formInfo, false);
    leads = leadsManagementService.getLeads();
    assertNotNull(leads);
    assertEquals(1, leads.size());
    assertEquals(1, leadsManagementService.getResponses(leads.get(0).getId()).length());
  }

  @Test
  public void deleteLead() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertNotNull(leadsManagementService.getLeadbyId(leadEntity.getId()));
    leadsManagementService.deleteLead(leadEntity);
    assertEquals(null, leadsManagementService.getLeadbyId(leadEntity.getId()));
  }

  @Test
  public void updateLead() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertEquals(language, leadEntity.getLanguage());
    leadEntity.setLanguage("fr");
    leadsManagementService.updateLead(leadsManagementService.toLeadDto(leadEntity));
    leadEntity = leadsManagementService.getLeadbyId(leadEntity.getId());
    assertEquals("fr", leadEntity.getLanguage());
  }

  @Test
  public void updateForm() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    FormEntity formEntity = newForm();
    assertEquals(formName, formEntity.getName());
    formEntity.setName("new name");
    formEntity = leadsManagementService.updateForm(formEntity);
    assertEquals("new name", formEntity.getName());
  }

  @Test
  public void assigneLead() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertEquals(assignee, leadEntity.getAssignee());
    leadsManagementService.assigneLead(leadEntity.getId(), "testUser");
    leadEntity = leadsManagementService.getLeadbyId(leadEntity.getId());
    assertEquals("testUser", leadEntity.getAssignee());
  }

  @Test
  public void updateStatus() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertEquals(status, leadEntity.getStatus());
    leadsManagementService.updateStatus(leadEntity.getId(), "Accepted");
    leadEntity = leadsManagementService.getLeadbyId(leadEntity.getId());
    assertEquals("Accepted", leadEntity.getStatus());
  }

  @Test
  public void getLeads() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    assertEquals(0, leadsManagementService.getLeads().size());
    newLead();
    assertEquals(1, leadsManagementService.getLeads().size());
  }

  @Test
  public void getResponses() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertEquals(0, leadsManagementService.getResponses(leadEntity.getId()).length());
    newResponse(newForm(), leadEntity);
    assertEquals(1, leadsManagementService.getResponses(leadEntity.getId()).length());
  }

  @Test
  public void addResponse() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertEquals(0, leadsManagementService.getResponses(leadEntity.getId()).length());
    leadsManagementService.addResponse(newResponseDTO(), leadEntity);
    assertEquals(1, leadsManagementService.getResponses(leadEntity.getId()).length());
  }

  @Test
  public void getLeadbyId() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertNotNull(leadsManagementService.getLeadbyId(leadEntity.getId()));
  }

  @Test
  public void getLeadByMail() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    assertNotNull(leadsManagementService.getLeadByMail(mail));
  }

  @Test
  public void mergeLead() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    assertNotNull(leadsManagementService.mergeLead(newLead(), newLeadDto()));
  }

  @Test
  public void toFormJson() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    try {
      assertNotNull(leadsManagementService.toFormJson(newForm()));
    } catch (JSONException e) {
      fail();
    }
  }

  @Test
  public void toLeadDto() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadEntity leadEntity = newLead();
    LeadDTO leadDTO = leadsManagementService.toLeadDto(leadEntity);
    compareLeadResult(leadEntity, leadDTO);
  }

  @Test
  public void toLeadEntity() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    LeadDTO leadDTO = newLeadDto();
    LeadEntity leadEntity = leadsManagementService.toLeadEntity(leadDTO);
    compareLeadResult(leadEntity, leadDTO);
  }

  @Test
  public void toFieldDto() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    ResponseEntity responseEntity = newResponse(newForm(), newLead());
    FieldEntity fieldEntity = newField(responseEntity);
    FieldDTO fieldDTO = leadsManagementService.toFieldDto(fieldEntity);
    compareFieldResult(fieldEntity, fieldDTO);
  }

  @Test
  public void toFieldEntity() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    FieldDTO fieldDTO = newFieldDTO();
    FieldEntity fieldEntity = leadsManagementService.toFieldEntity(fieldDTO);
    compareFieldResult(fieldEntity, fieldDTO);
  }

  @Test
  public void toFormDto() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    FormEntity formEntity = newForm();
    FormDTO formDTO = leadsManagementService.toFormDto(formEntity);
    compareFormResult(formEntity, formDTO);
  }

  @Test
  public void toFormEntity() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    FormDTO formDTO = newFormDto();
    FormEntity formEntity = leadsManagementService.toFormEntity(formDTO);
    compareFormResult(formEntity, formDTO);
  }

  @Test
  public void toResponseDto() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    ResponseEntity responseEntity = newResponse(newForm(), newLead());
    ResponseDTO responseDTO = leadsManagementService.toResponseDto(responseEntity);
    compareResponseResult(responseEntity, responseDTO);

  }

  @Test
  public void toResponseEntity() {
    LeadsManagementService leadsManagementService = getService(LeadsManagementService.class);
    ResponseDTO responseDTO = newResponseDTO();
    ResponseEntity responseEntity = leadsManagementService.toResponseEntity(responseDTO);
    compareResponseResult(responseEntity, responseDTO);
  }

}
