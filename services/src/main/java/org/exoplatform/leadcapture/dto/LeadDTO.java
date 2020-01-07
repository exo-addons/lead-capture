package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class LeadDTO implements Serializable {

  private Long    id;

  private String  mail;

  private String  firstName;

  private String  lastName;

  private String  company;

  private String  position;

  private String  country;

  private String  status;

  private String  phone;

  private Long    createdDate;

  private Long    updatedDate;

  private String  language;

  private String  assignee;

  private String  geographiqueZone;

  private Boolean marketingSuspended;

  private String  marketingSuspendedCause;

  private String  captureMethod;

  private String  captureType;

  private Boolean blogSubscription;

  private Long    blogSubscriptionDate;

  private String  communityUserName;

  private Boolean communityRegistration;

  private String  communityRegistrationMethod;

  private Long    communityRegistrationDate;

  private String  personSource;

  private String  landingPageInfo;

  private String  captureSourceInfo;

  private String  personIp;

  private String  originalReferrer;

  private String  formattedCreatedDate;

  private String  formattedUpdatedDate;

  private String  activityId;

  private Long  taskId;

  private String  taskUrl;
}
