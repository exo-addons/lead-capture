package org.exoplatform.leadcapture.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dao.ResponseDAO;
import org.exoplatform.leadcapture.dto.FieldDTO;
import org.exoplatform.leadcapture.dto.FormDTO;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.dto.ResponseDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;

public abstract class BaseLeadManagementTest {

  protected static PortalContainer container;

  protected String  mail = "test@exoplatform.com";

  protected String  firstName ="User";

  protected String  lastName = "Test";

  protected String  company = "eXo";

  protected String  position = "Manager";

  protected String  inferredCountry ="Tunisia";

  protected String  status ="opened";

  protected String  language = "en";

  protected String  assignee ="root";

  protected String  phone ="2222222222";

  protected String  geographiqueZone ="geographiqueZone";

  protected boolean  marketingSuspended =false;

  protected String  marketingSuspendedCause ="";

  protected String  captureMethod ="captureMethod";

  protected String  captureType ="captureType";

  protected boolean  blogSubscription =true;

  protected String  communityUserName ="communityUserName";

  protected boolean  communityRegistration =true;

  protected String  communityRegistrationMethod ="communityRegistrationMethod";

  protected String  personSource ="personSource";

  protected String  landingPageInfo ="landingPageInfo";

  protected String  captureSourceInfo ="captureSourceInfo";

  protected String  personIp ="personIp";

  protected String  originalReferrer ="originalReferrer";

  protected String formName = "newForm";

  protected String formFields ="name,message,createdDate";

  protected String  fieldName = "fieldName";

  protected String  fieldValue = "fieldValue";


  @BeforeClass
  public static void beforeTest() {
    container = PortalContainer.getInstance();
    assertNotNull(container);
    assertTrue(container.isStarted());
  }

  @Before
  public void beforeMethodTest() {
    RequestLifeCycle.begin(container);
  }

  @After
  public void afterMethodTest() {
    LeadDAO leadDAO = getService(LeadDAO.class);
    FormDAO formDAO = getService(FormDAO.class);
    ResponseDAO responseDAO = getService(ResponseDAO.class);
    FieldDAO fieldDAO = getService(FieldDAO.class);

    RequestLifeCycle.end();
    RequestLifeCycle.begin(container);
    fieldDAO.deleteAll();
    responseDAO.deleteAll();
    leadDAO.deleteAll();
    formDAO.deleteAll();
    RequestLifeCycle.end();
  }

  protected <T> T getService(Class<T> componentType) {
    return container.getComponentInstanceOfType(componentType);
  }

  protected LeadEntity newLead() {
    LeadDAO leadDAO = getService(LeadDAO.class);
    LeadEntity leadEntity = new LeadEntity();
    leadEntity.setMail(mail);
    leadEntity.setFirstName(firstName);
    leadEntity.setLastName(lastName);
    leadEntity.setCompany(company);
    leadEntity.setPosition(position);
    leadEntity.setCountry(inferredCountry);
    leadEntity.setStatus(status);
    leadEntity.setLanguage(language);
    leadEntity.setAssignee(assignee);
    leadEntity.setPhone(phone);
    leadEntity.setCreatedDate(new Date());
    leadEntity.setUpdatedDate(new Date());
    leadEntity.setGeographiqueZone(geographiqueZone);
    leadEntity.setMarketingSuspended(marketingSuspended);
    leadEntity.setMarketingSuspendedCause(marketingSuspendedCause);
    leadEntity.setCaptureMethod(captureMethod);
    leadEntity.setCaptureType(captureType);
    leadEntity.setBlogSubscription(blogSubscription);
    leadEntity.setBlogSubscriptionDate(new Date());
    leadEntity.setCommunityUserName(communityUserName);
    leadEntity.setCommunityRegistration(communityRegistration);
    leadEntity.setCommunityRegistrationMethod(communityRegistrationMethod);
    leadEntity.setCommunityRegistrationDate(new Date());
    leadEntity.setPersonSource(personSource);
    leadEntity.setLandingPageInfo(landingPageInfo);
    leadEntity.setCaptureSourceInfo(captureSourceInfo);
    leadEntity.setPersonIp(personIp);
    leadEntity.setOriginalReferrer(originalReferrer);
    leadEntity = leadDAO.create(leadEntity);
    return leadEntity;
  }

  protected LeadDTO newLeadDto() {
    LeadDTO leadDTO = new LeadDTO();
    leadDTO.setMail(mail);
    leadDTO.setFirstName(firstName);
    leadDTO.setLastName(lastName);
    leadDTO.setCompany(company);
    leadDTO.setPosition(position);
    leadDTO.setInferredCountry(inferredCountry);
    leadDTO.setStatus(status);
    leadDTO.setLanguage(language);
    leadDTO.setAssignee(assignee);
    leadDTO.setPhone(phone);
    leadDTO.setGeographiqueZone(geographiqueZone);
    leadDTO.setMarketingSuspended(marketingSuspended);
    leadDTO.setMarketingSuspendedCause(marketingSuspendedCause);
    leadDTO.setCaptureMethod(captureMethod);
    leadDTO.setCaptureType(captureType);
    leadDTO.setBlogSubscription(blogSubscription);
    leadDTO.setBlogSubscriptionDate(new Date());
    leadDTO.setCommunityUserName(communityUserName);
    leadDTO.setCommunityRegistration(communityRegistration);
    leadDTO.setCommunityRegistrationMethod(communityRegistrationMethod);
    leadDTO.setCommunityRegistrationDate(new Date());
    leadDTO.setPersonSource(personSource);
    leadDTO.setLandingPageInfo(landingPageInfo);
    leadDTO.setCaptureSourceInfo(captureSourceInfo);
    leadDTO.setPersonIp(personIp);
    leadDTO.setOriginalReferrer(originalReferrer);
    return leadDTO;
  }

  protected FormEntity newForm() {
    FormDAO formDAO = getService(FormDAO.class);
    FormEntity formEntity = new FormEntity();
    formEntity.setName(formName);
    formEntity.setFields(formFields);
    formEntity = formDAO.create(formEntity);
    return formEntity;
  }

  protected FormDTO newFormDto() {
    FormDTO formDTO = new FormDTO();
    formDTO.setName(formName);
    formDTO.setFields(formFields);
    return formDTO;
  }

  protected ResponseEntity newResponse(FormEntity formEntity,LeadEntity leadEntity) {
    ResponseDAO responseDAO = getService(ResponseDAO.class);
    ResponseEntity responseEntity = new ResponseEntity();
    responseEntity.setFormEntity(formEntity);
    responseEntity.setLeadEntity(leadEntity);
    responseEntity.setCreatedDate(new Date());
    responseEntity = responseDAO.create(responseEntity);
    return responseEntity;
  }

  protected ResponseDTO newResponseDTO() {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setForm(newFormDto());
    responseDTO.setFormName(responseDTO.getForm().getName());
    List<FieldDTO> fields = new ArrayList<>();
    fields.add(newFieldDTO());
    responseDTO.setFields(fields);
    return responseDTO;
  }

  protected FieldEntity newField(ResponseEntity responseEntity) {
    FieldDAO fieldDAO = getService(FieldDAO.class);
    FieldEntity fieldEntity = new FieldEntity();
    fieldEntity.setName(fieldName);
    fieldEntity.setValue(fieldValue);
    fieldEntity.setResponseEntity(responseEntity);
    fieldEntity = fieldDAO.create(fieldEntity);
    return fieldEntity;
  }

  protected FieldDTO newFieldDTO() {
    FieldDTO fieldDTO = new FieldDTO();
    fieldDTO.setName(fieldName);
    fieldDTO.setValue(fieldValue);
    return fieldDTO;
  }

  protected void compareLeadResult(LeadEntity leadEntity, LeadDTO leadDTO){
    assertEquals(leadEntity.getId(), leadDTO.getId());
    assertEquals(leadEntity.getMail(), leadDTO.getMail() );
    assertEquals(leadEntity.getFirstName(), leadDTO.getFirstName());
    assertEquals(leadEntity.getLastName() , leadDTO.getLastName() );
    assertEquals(leadEntity.getCompany() , leadDTO.getCompany() );
    assertEquals(leadEntity.getPosition(), leadDTO.getPosition());
    assertEquals(leadEntity.getCountry(), leadDTO.getInferredCountry());
    assertEquals(leadEntity.getStatus(), leadDTO.getStatus());
    assertEquals(leadEntity.getPhone(), leadDTO.getPhone());
    assertEquals(leadEntity.getCreatedDate(), leadDTO.getCreatedDate());
    assertEquals(leadEntity.getUpdatedDate(), leadDTO.getUpdatedDate());
    assertEquals(leadEntity.getLanguage(), leadDTO.getLanguage());
    assertEquals(leadEntity.getAssignee(), leadDTO.getAssignee());
    assertEquals(leadEntity.getGeographiqueZone(), leadDTO.getGeographiqueZone());
    assertEquals(leadEntity.getMarketingSuspended(), leadDTO.getMarketingSuspended());
    assertEquals(leadEntity.getMarketingSuspendedCause(), leadDTO.getMarketingSuspendedCause());
    assertEquals(leadEntity.getCaptureMethod(), leadDTO.getCaptureMethod());
    assertEquals(leadEntity.getCaptureType(), leadDTO.getCaptureType());
    assertEquals(leadEntity.getBlogSubscription(), leadDTO.getBlogSubscription());
    assertEquals(leadEntity.getBlogSubscriptionDate(), leadDTO.getBlogSubscriptionDate());
    assertEquals(leadEntity.getCommunityUserName(), leadDTO.getCommunityUserName());
    assertEquals(leadEntity.getCommunityRegistration(), leadDTO.getCommunityRegistration());
    assertEquals(leadEntity.getCommunityRegistrationMethod(), leadDTO.getCommunityRegistrationMethod());
    assertEquals(leadEntity.getCommunityRegistrationDate(), leadDTO.getCommunityRegistrationDate());
    assertEquals(leadEntity.getPersonSource(), leadDTO.getPersonSource());
    assertEquals(leadEntity.getLandingPageInfo(), leadDTO.getLandingPageInfo());
    assertEquals(leadEntity.getCaptureSourceInfo(), leadDTO.getCaptureSourceInfo());
    assertEquals(leadEntity.getPersonIp(), leadDTO.getPersonIp());
    assertEquals(leadEntity.getOriginalReferrer(), leadDTO.getOriginalReferrer());
  }

  protected void compareFormResult(FormEntity formEntity, FormDTO formDTO){
    assertEquals(formEntity.getId(), formDTO.getId());
    assertEquals(formEntity.getName(), formDTO.getName());
    assertEquals(formEntity.getFields(), formDTO.getFields());
  }

  protected void compareResponseResult(ResponseEntity responseEntity, ResponseDTO responseDTO){
    assertEquals(responseEntity.getId(), responseDTO.getId());
    compareFormResult(responseEntity.getFormEntity(),responseDTO.getForm());
  }

  protected void compareFieldResult(FieldEntity fieldEntity, FieldDTO fieldDTO){
    assertEquals(fieldEntity.getId(), fieldDTO.getId());
    assertEquals(fieldEntity.getName(), fieldDTO.getName());
    assertEquals(fieldEntity.getValue(), fieldDTO.getValue());
  }
}



