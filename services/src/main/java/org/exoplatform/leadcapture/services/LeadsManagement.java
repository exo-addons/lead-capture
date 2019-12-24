package org.exoplatform.leadcapture.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.leadcapture.dto.*;
import org.exoplatform.services.listener.ListenerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dao.ResponseDAO;
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

  private ListenerService listenerService;

  public LeadsManagement(LeadDAO leadDAO, FormDAO formDAO, FieldDAO fieldDAO, ResponseDAO responseDAO, ListenerService listenerService) {
    this.leadDAO = leadDAO;
    this.formDAO = formDAO;
    this.fieldDAO = fieldDAO;
    this.responseDAO = responseDAO;
    this.listenerService = listenerService;
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
        leadEntity = leadDAO.create(toLeadEntity(lead));
        listenerService.broadcast("leadCapture.newLead.event", leadEntity, "");
      } else {
        leadEntity = mergeLead(leadEntity, lead);
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
      leadDAO.update(toLeadEntity(lead));
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
          leadsList.add(toLeadDto(leadEntity));
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
        formResponse.put("form", toFormJson(formEntity));
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

  public static LeadDTO toLeadDto(LeadEntity leadEntity) {
    LeadDTO leadDTO = new LeadDTO();
    leadDTO.setId(leadEntity.getId());
    leadDTO.setMail(leadEntity.getMail());
    leadDTO.setFirstName(leadEntity.getFirstName());
    leadDTO.setLastName(leadEntity.getLastName());
    leadDTO.setCompany(leadEntity.getCompany());
    leadDTO.setPosition(leadEntity.getPosition());
    leadDTO.setCountry(leadEntity.getCountry());
    leadDTO.setStatus(leadEntity.getStatus());
    leadDTO.setPhone(leadEntity.getPhone());
    leadDTO.setCreatedDate(leadEntity.getCreatedDate());
    leadDTO.setFormattedCreatedDate(Utils.getFormatter().format(new Date(leadEntity.getCreatedDate())));
    leadDTO.setUpdatedDate(leadEntity.getUpdatedDate());
    leadDTO.setFormattedUpdatedDate(Utils.getFormatter().format(new Date(leadEntity.getUpdatedDate())));
    leadDTO.setLanguage(leadEntity.getLanguage());
    leadDTO.setAssignee(leadEntity.getAssignee());
    leadDTO.setGeographiqueZone(leadEntity.getGeographiqueZone());
    leadDTO.setMarketingSuspended(leadEntity.getMarketingSuspended());
    leadDTO.setMarketingSuspendedCause(leadEntity.getMarketingSuspendedCause());
    leadDTO.setCaptureMethod(leadEntity.getCaptureMethod());
    leadDTO.setCaptureType(leadEntity.getCaptureType());
    leadDTO.setBlogSubscription(leadEntity.getBlogSubscription());
    leadDTO.setBlogSubscriptionDate(leadEntity.getBlogSubscriptionDate());
    leadDTO.setCommunityUserName(leadEntity.getCommunityUserName());
    leadDTO.setCommunityRegistration(leadEntity.getCommunityRegistration());
    leadDTO.setCommunityRegistrationMethod(leadEntity.getCommunityRegistrationMethod());
    leadDTO.setCommunityRegistrationDate(leadEntity.getCommunityRegistrationDate());
    leadDTO.setPersonSource(leadEntity.getPersonSource());
    leadDTO.setLandingPageInfo(leadEntity.getLandingPageInfo());
    leadDTO.setCaptureSourceInfo(leadEntity.getCaptureSourceInfo());
    leadDTO.setPersonIp(leadEntity.getPersonIp());
    leadDTO.setOriginalReferrer(leadEntity.getOriginalReferrer());
    return leadDTO;
  }

  public static LeadEntity toLeadEntity(LeadDTO leadDTO) {
    LeadEntity leadEntity = new LeadEntity();
    leadEntity.setId(leadDTO.getId());
    leadEntity.setMail(leadDTO.getMail());
    leadEntity.setFirstName(leadDTO.getFirstName());
    leadEntity.setLastName(leadDTO.getLastName());
    leadEntity.setCompany(leadDTO.getCompany());
    leadEntity.setPosition(leadDTO.getPosition());
    leadEntity.setCountry(leadDTO.getCountry());
    leadEntity.setStatus(leadDTO.getStatus());
    leadEntity.setPhone(leadDTO.getPhone());
    leadEntity.setCreatedDate(leadDTO.getCreatedDate());
    leadEntity.setUpdatedDate(leadDTO.getUpdatedDate());
    leadEntity.setLanguage(leadDTO.getLanguage());
    leadEntity.setAssignee(leadDTO.getAssignee());
    leadEntity.setGeographiqueZone(leadDTO.getGeographiqueZone());
    leadEntity.setMarketingSuspended(leadDTO.getMarketingSuspended());
    leadEntity.setMarketingSuspendedCause(leadDTO.getMarketingSuspendedCause());
    leadEntity.setCaptureMethod(leadDTO.getCaptureMethod());
    leadEntity.setCaptureType(leadDTO.getCaptureType());
    leadEntity.setBlogSubscription(leadDTO.getBlogSubscription());
    leadEntity.setBlogSubscriptionDate(leadDTO.getBlogSubscriptionDate());
    leadEntity.setCommunityUserName(leadDTO.getCommunityUserName());
    leadEntity.setCommunityRegistration(leadDTO.getCommunityRegistration());
    leadEntity.setCommunityRegistrationMethod(leadDTO.getCommunityRegistrationMethod());
    leadEntity.setCommunityRegistrationDate(leadDTO.getCommunityRegistrationDate());
    leadEntity.setPersonSource(leadDTO.getPersonSource());
    leadEntity.setLandingPageInfo(leadDTO.getLandingPageInfo());
    leadEntity.setCaptureSourceInfo(leadDTO.getCaptureSourceInfo());
    leadEntity.setPersonIp(leadDTO.getPersonIp());
    leadEntity.setOriginalReferrer(leadDTO.getOriginalReferrer());
    return leadEntity;
  }

  public static LeadEntity mergeLead(LeadEntity leadEntity, LeadDTO leadDTO) {

    if (!StringUtils.isEmpty(leadDTO.getFirstName()))
      leadEntity.setFirstName(leadDTO.getFirstName());
    if (!StringUtils.isEmpty(leadDTO.getLastName()))
      leadEntity.setLastName(leadDTO.getLastName());
    if (!StringUtils.isEmpty(leadDTO.getCompany()))
      leadEntity.setCompany(leadDTO.getCompany());
    if (!StringUtils.isEmpty(leadDTO.getPosition()))
      leadEntity.setPosition(leadDTO.getPosition());
    if (!StringUtils.isEmpty(leadDTO.getCountry()))
      leadEntity.setCountry(leadDTO.getCountry());
    if (!StringUtils.isEmpty(leadDTO.getStatus()))
      leadEntity.setStatus(leadDTO.getStatus());
    if (!StringUtils.isEmpty(leadDTO.getPhone()))
      leadEntity.setPhone(leadDTO.getPhone());
    if (!StringUtils.isEmpty(leadDTO.getLanguage()))
      leadEntity.setLanguage(leadDTO.getLanguage());
    if (!StringUtils.isEmpty(leadDTO.getAssignee()))
      leadEntity.setAssignee(leadDTO.getAssignee());
    if (!StringUtils.isEmpty(leadDTO.getGeographiqueZone()))
      leadEntity.setGeographiqueZone(leadDTO.getGeographiqueZone());
    if (!StringUtils.isEmpty(leadDTO.getCaptureMethod()))
      leadEntity.setCaptureMethod(leadDTO.getCaptureMethod());
    if (!StringUtils.isEmpty(leadDTO.getCaptureType()))
      leadEntity.setCaptureType(leadDTO.getCaptureType());
    if (leadDTO.getBlogSubscription() != null && leadDTO.getBlogSubscription() && !leadEntity.getBlogSubscription()) {
      leadEntity.setBlogSubscription(leadDTO.getBlogSubscription());
      leadEntity.setBlogSubscriptionDate(new Date().getTime());
    }
    if (!StringUtils.isEmpty(leadDTO.getCommunityUserName()))
      leadEntity.setCommunityUserName(leadDTO.getCommunityUserName());
    if (leadDTO.getCommunityRegistration() != null)
      leadEntity.setCommunityRegistration(leadDTO.getCommunityRegistration());
    if (!StringUtils.isEmpty(leadDTO.getCommunityRegistrationMethod()))
      leadEntity.setCommunityRegistrationMethod(leadDTO.getCommunityRegistrationMethod());
    if (leadDTO.getCommunityRegistrationDate() != null)
      leadEntity.setCommunityRegistrationDate(leadDTO.getCommunityRegistrationDate());
    return leadEntity;
  }

  public static FieldDTO toFieldDto(FieldEntity fieldEntity) {
    FieldDTO fieldDTO = new FieldDTO();
    fieldDTO.setId(fieldEntity.getId());
    fieldDTO.setName(fieldEntity.getName());
    fieldDTO.setValue(fieldEntity.getValue());
    return fieldDTO;
  }

  public static FieldEntity toFieldEntity(FieldDTO fieldDTO) {
    FieldEntity fieldEntity = new FieldEntity();
    fieldEntity.setId(fieldDTO.getId());
    fieldEntity.setName(fieldDTO.getName());
    fieldEntity.setValue(fieldDTO.getValue());
    return fieldEntity;
  }

  public static FormDTO toFormDto(FormEntity formEntity) {
    FormDTO formDTO = new FormDTO();
    formDTO.setId(formEntity.getId());
    formDTO.setName(formEntity.getName());
    formDTO.setFields(formEntity.getFields());
    return formDTO;
  }

  public static FormEntity toFormEntity(FormDTO formDTO) {
    FormEntity formEntity = new FormEntity();
    formEntity.setId(formDTO.getId());
    formEntity.setName(formDTO.getName());
    formEntity.setFields(formDTO.getFields());
    return formEntity;
  }

  public static ResponseDTO toResponseDto(ResponseEntity responseEntity) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setId(responseEntity.getId());
    responseDTO.setForm(toFormDto(responseEntity.getFormEntity()));
    List<FieldDTO> fields = new ArrayList<>();
    for (FieldEntity fieald : responseEntity.getFilelds()) {
      fields.add(toFieldDto(fieald));
    }
    responseDTO.setFields(fields);
    return responseDTO;
  }

  public static ResponseEntity toResponseEntity(ResponseDTO responseDTO) {
    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setId(responseDTO.getId());
    responseEntity.setFormEntity(toFormEntity(responseDTO.getForm()));
    List<FieldEntity> fields = new ArrayList<>();
    for (FieldDTO fieald : responseDTO.getFields()) {
      fields.add(toFieldEntity(fieald));
    }
    responseEntity.setFilelds(fields);
    return responseEntity;
  }

  public static JSONObject toFormJson(FormEntity formEntity) throws JSONException {
    JSONObject formJson = new JSONObject();
    // JsonArray fields = new JsonArray();
    /*
     * for (String field : formEntity.getFields().split(",")){ fields.add(field); }
     */
    formJson.put("id", formEntity.getId());
    formJson.put("name", formEntity.getName());
    formJson.put("fields", formEntity.getFields().split(","));
    return formJson;
  }

}
