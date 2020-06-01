package org.exoplatform.leadcapture.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "LeadEntity")
@ExoEntity
@Table(name = "ADDONS_LC_LEAD")
@Data
@NamedQueries({
    @NamedQuery(name = "LeadEntity.countLeads", query = "SELECT count(lead.id) FROM  LeadEntity lead"),
    @NamedQuery(name = "LeadEntity.getLeadByMail", query = "SELECT lead FROM LeadEntity lead where lead.mail = :mail "),
    @NamedQuery(name = "LeadEntity.getLeadsByStatus", query = "SELECT lead FROM LeadEntity lead where lead.status = :status "),
    @NamedQuery(name = "LeadEntity.getLeadByTask", query = "SELECT lead FROM LeadEntity lead where lead.taskId = :taskId ") })



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
  protected Date    createdDate;

  @Column(name = "UPDATED_DATE")
  protected Date    updatedDate;

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
  protected Date    blogSubscriptionDate;

  @Column(name = "COMMUNITY_USER_NAME")
  protected String  communityUserName;

  @Column(name = "COMMUNITY_REGISTRATION")
  protected Boolean communityRegistration;

  @Column(name = "COMMUNITY_REGISTRATION_METHOD")
  protected String  communityRegistrationMethod;

  @Column(name = "COMMUNITY_REGISTRATION_DATE")
  protected Date    communityRegistrationDate;

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

  @Column(name = "ACTIVITY_ID")
  protected String  activityId;

  @Column(name = "TASK_ID")
  protected Long    taskId;

  @Column(name = "TASK_URL")
  protected String  taskUrl;

  @Column(name = "GOAL")
  protected String  goal;

  @Column(name = "USERS_NUMBER")
  protected String  usersNumber;

  @Column(name = "CURRENT_SOLUTION")
  protected String  currentSolution;

  @Column(name = "INTERACTION_SUMMARY")
  protected String  interactionSummary;

  @Column(name = "HOW_HEAR")
  protected String  howHear;

  @Column(name = "SOLUTION_TYPE")
  protected String  solutionType;

  @Column(name = "SOLUTION_REQUIEREMENTS")
  protected String  solutionRequirements;

  @Column(name = "SHORTLIST_VENDORS")
  protected String  shortlistVendors;

  @Column(name = "COMPANY_WEBSITE")
  protected String  companyWebsite;

  @Column(name = "EMPLOYEES_NUMBER")
  protected String  employeesNumber;

  @Column(name = "INDUSTRY")
  protected String  industry;

  @Column(name = "TASKS_LABEL_ID")
  protected Long  tasksLabelId;

}
