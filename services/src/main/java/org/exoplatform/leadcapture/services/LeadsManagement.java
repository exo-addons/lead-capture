package org.exoplatform.leadcapture.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dao.ResponseDAO;
import org.exoplatform.leadcapture.dto.FieldDTO;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.dto.ResponseDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class LeadsManagement {

  private final Log   LOG = ExoLogger.getLogger(LeadsManagement.class);

  private LeadDAO     leadDAO;

  private FormDAO     formDAO;

  private FieldDAO    fieldDAO;

  private ResponseDAO responseDAO;

  public LeadsManagement(LeadDAO leadDAO, FormDAO formDAO, FieldDAO fieldDAO, ResponseDAO responseDAO) {
    this.leadDAO = leadDAO;
    this.formDAO = formDAO;
    this.fieldDAO = fieldDAO;
    this.responseDAO = responseDAO;
  }

  public void addLeadInfo(FormInfo leadInfo) throws IOException {
    try {
      LeadDTO lead = leadInfo.getLead();
      LeadEntity leadEntity = leadDAO.getLeadByMail(lead.getMail());
      if (leadEntity == null) {
        lead.setCreatedDate(new Date().getTime());
        lead.setUpdatedDate(new Date().getTime());
        if (lead.getBlogSubscription() != null && lead.getBlogSubscription()) {
          leadEntity.setBlogSubscription(true);
          leadEntity.setBlogSubscriptionDate(new Date().getTime());
        }
        leadEntity = leadDAO.create(Utils.toLeadEntity(lead));
      } else {
        leadEntity = Utils.mergeLead(leadEntity, lead);
        leadEntity.setUpdatedDate(new Date().getTime());
        leadEntity = leadDAO.update(leadEntity);
      }
      addResponse(leadInfo.getResponse(), leadEntity);
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  public void deleteLead(LeadEntity lead) throws IOException {
    try {
      List<FieldEntity> fieldEntities = new ArrayList<>();
      List<ResponseEntity> responseEntities = responseDAO.getResponsesByLead(lead.getId());
      for (ResponseEntity responseEntity : responseEntities) {
        fieldDAO.deleteAll(fieldDAO.getFileldsByResponse(responseEntity.getId()));
      }
      responseDAO.deleteAll(responseEntities);
      leadDAO.delete(lead);
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  public void updateLead(LeadDTO lead) throws IOException {
    try {
      lead.setUpdatedDate(new Date().getTime());
      leadDAO.update(Utils.toLeadEntity(lead));
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  public void assigneLead(Long leadId, String assignee) throws IOException {
    try {
      LeadEntity leadEntity = leadDAO.find(leadId);
      leadEntity.setUpdatedDate(new Date().getTime());
      leadEntity.setAssignee(assignee);
      leadDAO.update(leadEntity);
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  public void updateStatus(Long leadId, String status) throws IOException {
    try {
      LeadEntity leadEntity = leadDAO.find(leadId);
      leadEntity.setUpdatedDate(new Date().getTime());
      leadEntity.setStatus(status);
      leadDAO.update(leadEntity);
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  public List<LeadDTO> getLeads() {
    List<LeadDTO> leadsList = new ArrayList<>();
    List<LeadEntity> leadsEntities = leadDAO.findAll();
    if (leadsEntities != null) {
      for (LeadEntity leadEntity : leadsEntities) {
        if (leadEntity != null) {
          leadsList.add(Utils.toLeadDto(leadEntity));
        }
      }
    }
    return leadsList;
  }

  public JSONArray getResponses(long leadId) {
    JSONArray formResponsesList = new JSONArray();
    List<FormEntity> formEntities = formDAO.findAll();
    for (FormEntity formEntity : formEntities) {
      try {
        JSONObject formResponse = new JSONObject();
        formResponse.put("form", Utils.toFormJson(formEntity));
        List<ResponseEntity> responsesEntities = responseDAO.getResponsesByForm(formEntity.getId(), leadId);
        if (responsesEntities != null && responsesEntities.size() > 0) {
          JSONArray responsesList = new JSONArray();
          for (ResponseEntity responseEntity : responsesEntities) {

            if (responseEntity != null) {
              responseEntity.setFilelds(fieldDAO.getFileldsByResponse(responseEntity.getId()));
              responsesList.put(Utils.toResponseJson(responseEntity));
            }
          }
          formResponse.put("responses", responsesList);
          formResponsesList.put(formResponse);
        }
      } catch (Exception e) {
        LOG.error("Cannot get responses for form {}", formEntity.getName(), e);
      }

    }

    return formResponsesList;
  }

  public void addResponse(ResponseDTO responseDTO, LeadEntity leadEntity) {

    FormEntity formEntity = formDAO.getFormByName(responseDTO.getFormName());
    if (formEntity == null) {
      String fields = responseDTO.getFields().stream().map(n -> n.getName()).collect(Collectors.joining(","));
      if (!fields.contains("createdDate")) {
        fields = (fields.concat(",createdDate"));
      }
      formEntity = formDAO.create(new FormEntity(responseDTO.getFormName(), fields));
    } else {
      String fields = formEntity.getFields();
      List<String> fieldList = new ArrayList<String>(Arrays.asList(fields.split(",")));
      if (!fieldList.contains("createdDate")) {
        fieldList.add("createdDate");
      }
      boolean changed = false;
      for (FieldDTO field : responseDTO.getFields()) {
        if (!fieldList.contains(field.getName())) {
          fieldList.add(field.getName());
          changed = true;
        }
      }
      if (changed) {
        fields = fieldList.stream().collect(Collectors.joining(","));
        formEntity.setFields(fields);
        formDAO.update(formEntity);
      }
    }
    ResponseEntity responseEntity = new ResponseEntity(formEntity, leadEntity);
    responseEntity.setCreatedDate(new Date().getTime());
    responseEntity = responseDAO.create(responseEntity);

    for (FieldDTO field : responseDTO.getFields()) {
      FieldEntity fieldEntity = new FieldEntity(field.getName(), field.getValue(), responseEntity);
      fieldDAO.create(fieldEntity);
    }
  }

  public LeadEntity getLeadbyId(long id) {
    return leadDAO.find(id);
  }

  public LeadEntity getLeadByMail(String mail) {
    return leadDAO.getLeadByMail(mail);
  }

}
