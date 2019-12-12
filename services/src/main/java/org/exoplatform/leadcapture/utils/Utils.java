package org.exoplatform.leadcapture.utils;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.leadcapture.dto.FieldDTO;
import org.exoplatform.leadcapture.dto.FormDTO;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.dto.ResponseDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {


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
    leadDTO.setUpdatedDate(leadEntity.getUpdatedDate());
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

    if(!StringUtils.isEmpty(leadDTO.getFirstName()))leadEntity.setFirstName(leadDTO.getFirstName());
    if(!StringUtils.isEmpty(leadDTO.getLastName()))leadEntity.setLastName(leadDTO.getLastName());
    if(!StringUtils.isEmpty(leadDTO.getCompany()))leadEntity.setCompany(leadDTO.getCompany());
    if(!StringUtils.isEmpty(leadDTO.getPosition()))leadEntity.setPosition(leadDTO.getPosition());
    if(!StringUtils.isEmpty(leadDTO.getCountry()))leadEntity.setCountry(leadDTO.getCountry());
    if(!StringUtils.isEmpty(leadDTO.getStatus()))leadEntity.setStatus(leadDTO.getStatus());
    if(!StringUtils.isEmpty(leadDTO.getPhone()))leadEntity.setPhone(leadDTO.getPhone());
    if(!StringUtils.isEmpty(leadDTO.getLanguage()))leadEntity.setLanguage(leadDTO.getLanguage());
    if(!StringUtils.isEmpty(leadDTO.getAssignee()))leadEntity.setAssignee(leadDTO.getAssignee());
    if(!StringUtils.isEmpty(leadDTO.getGeographiqueZone()))leadEntity.setGeographiqueZone(leadDTO.getGeographiqueZone());
    if(!StringUtils.isEmpty(leadDTO.getCaptureMethod()))leadEntity.setCaptureMethod(leadDTO.getCaptureMethod());
    if(!StringUtils.isEmpty(leadDTO.getCaptureType()))leadEntity.setCaptureType(leadDTO.getCaptureType());
    if(leadDTO.getBlogSubscription()!=null && leadDTO.getBlogSubscription() && !leadEntity.getBlogSubscription())
    {
      leadEntity.setBlogSubscription(leadDTO.getBlogSubscription());
      leadEntity.setBlogSubscriptionDate(new Date().getTime());
    }
    if(!StringUtils.isEmpty(leadDTO.getCommunityUserName()))leadEntity.setCommunityUserName(leadDTO.getCommunityUserName());
    if(leadDTO.getCommunityRegistration()!=null)leadEntity.setCommunityRegistration(leadDTO.getCommunityRegistration());
    if(!StringUtils.isEmpty(leadDTO.getCommunityRegistrationMethod()))leadEntity.setCommunityRegistrationMethod(leadDTO.getCommunityRegistrationMethod());
    if(leadDTO.getCommunityRegistrationDate()!=null)leadEntity.setCommunityRegistrationDate(leadDTO.getCommunityRegistrationDate());
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
    for (FieldEntity fieald:responseEntity.getFilelds()){
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
    for (FieldDTO fieald:responseDTO.getFields()){
      fields.add(toFieldEntity(fieald));
    }
    responseEntity.setFilelds(fields);
    return responseEntity;
  }



}
