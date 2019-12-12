package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "LeadEntity")
@ExoEntity
@Table(name = "ADDONS_LC_LEAD")
@Data
  @NamedQueries({
  @NamedQuery(name = "LeadEntity.getLeadByMail", query =
  "SELECT lead FROM LeadEntity lead where lead.mail = :mail "
  ) })

public class LeadEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_LEAD_ID", sequenceName = "SEQ_ADDONS_LC_LEAD_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_LEAD_ID")
  @Column(name = "ID")
  protected Long    id;

  @Column(name = "MAIL", nullable = false, unique = true)
  protected String  mail;

  @Column(name = "FIRST_NAME")
  protected String  firstName;

  @Column(name = "LAST_NAME")
  protected String  lastName;

  @Column(name = "COMPANY")
  protected String  company;

  @Column(name = "POSITION")
  protected String  position;

  @Column(name = "COUNTRY")
  protected String  country;

  @Column(name = "STATUS")
  protected String  status;

  @Column(name = "PHONE")
  protected String  phone;

  @Column(name = "CREATED_DATE")
  protected Long    createdDate;

  @Column(name = "UPDATED_DATE")
  protected Long    updatedDate;

  @Column(name = "LANGUAGE")
  protected String  language;

  @Column(name = "ASSIGNEE")
  protected String  assignee;

  @Column(name = "GEOGRAPHIQUE_ZONE")
  protected String  geographiqueZone;

  @Column(name = "MARKETING_SUSPENDED")
  protected Boolean marketingSuspended;

  @Column(name = "MARKETING_SUSPENDED_CAUSE")
  protected String  marketingSuspendedCause;

  @Column(name = "CAPTURE_METHOD")
  protected String  captureMethod;

  @Column(name = "CAPTURE_TYPE")
  protected String  captureType;

  @Column(name = "BLOG_SUBSCRIPTION")
  protected Boolean blogSubscription;

  @Column(name = "BLOG_SUBSCRIPTION_DATE")
  protected Long  blogSubscriptionDate;

  @Column(name = "COMMUNITY_USER_NAME")
  protected String  communityUserName;

  @Column(name = "COMMUNITY_REGISTRATION")
  protected Boolean communityRegistration;

  @Column(name = "COMMUNITY_REGISTRATION_METHOD")
  protected String  communityRegistrationMethod;

  @Column(name = "COMMUNITY_REGISTRATION_DATE")
  protected Long  communityRegistrationDate;

  @Column(name = "PERSON_SOURCE")
  protected String  personSource;

  @Column(name = "LANDING_PAGE_INFO")
  protected String  landingPageInfo;

  @Column(name = "CAPTURE_SOURCE_INFO")
  protected String  captureSourceInfo;

  @Column(name = "PERSON_IP")
  protected String  personIp;

  @Column(name = "ORIGINAL_REFERRER")
  protected String  originalReferrer;

}
