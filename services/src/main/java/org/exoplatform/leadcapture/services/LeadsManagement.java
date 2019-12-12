package org.exoplatform.leadcapture.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dao.ResponseDAO;
import org.exoplatform.leadcapture.dto.*;
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

  public List<FormResponses> getResponses() {
    List <FormResponses> formResponsesList = new ArrayList<>();
    List<FormEntity> formEntities = formDAO.findAll();
    for (FormEntity formEntity : formEntities){
      List<ResponseEntity> responsesEntities = responseDAO.getResponsesByForm(formEntity.getId());
      if (responsesEntities != null) {
        List <ResponseDTO>  responsesList= new ArrayList<>();
        for (ResponseEntity responseEntity : responsesEntities) {
          
          if (responseEntity != null) {
            responsesList.add(Utils.toResponseDto(responseEntity));
          }
        }
        formResponsesList.add(new FormResponses(Utils.toFormDto(formEntity),responsesList));
      }
    }
        
    return formResponsesList;
  }

  public void addResponse(ResponseDTO responseDTO, LeadEntity leadEntity) {

    FormEntity formEntity = formDAO.getFormByName(responseDTO.getFormName());
    if (formEntity == null) {
      String fields = responseDTO.getFields().stream().map(n -> n.getName()).collect(Collectors.joining(","));
      formEntity = formDAO.create(new FormEntity(responseDTO.getFormName(), fields));
    } else {
      String fields = formEntity.getFields();
      List<String> fieldList = new ArrayList<String>(Arrays.asList(fields.split(",")));
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

}
