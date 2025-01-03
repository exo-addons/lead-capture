package org.exoplatform.leadcapture.services;

import static org.exoplatform.leadcapture.Utils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.exoplatform.commons.api.persistence.ExoTransactional;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dao.FormDAO;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dao.ResponseDAO;
import org.exoplatform.leadcapture.dto.*;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.task.domain.Priority;
import org.exoplatform.task.dto.*;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.exoplatform.task.service.*;
import org.exoplatform.task.util.ResourceUtil;
import org.exoplatform.task.util.TaskUtil;

public class LeadsManagementService {

  private final Log                  LOG = ExoLogger.getLogger(LeadsManagementService.class);

  private LeadDAO                    leadDAO;

  private FormDAO                    formDAO;

  private FieldDAO                   fieldDAO;

  private ResponseDAO                responseDAO;

  private ListenerService            listenerService;

  private TaskService                taskService;

  private StatusService              statusService;

  private ProjectService             projectService;

  private CommentService             commentService;

  private LabelService               labelService;

  private LeadCaptureSettingsService leadCaptureSettingsService;


  public LeadsManagementService(LeadDAO leadDAO,
                                FormDAO formDAO,
                                FieldDAO fieldDAO,
                                ResponseDAO responseDAO,
                                TaskService taskService,
                                StatusService statusService,
                                ProjectService projectService,
                                LabelService labelService,
                                CommentService commentService,
                                LeadCaptureSettingsService leadCaptureSettingsService,
                                ListenerService listenerService) {
    this.leadDAO = leadDAO;
    this.formDAO = formDAO;
    this.fieldDAO = fieldDAO;
    this.responseDAO = responseDAO;
    this.listenerService = listenerService;
    this.taskService = taskService;
    this.statusService = statusService;
    this.projectService = projectService;
    this.labelService = labelService;
    this.commentService = commentService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  public LeadEntity addLeadInfo(FormInfo leadInfo, boolean broadcast) throws Exception {
    LeadEntity leadEntity = null;
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    try {
      LeadDTO lead = leadInfo.getLead();
      leadEntity = leadDAO.getLeadByMail(lead.getMail());
      if (leadEntity == null) {
        if (lead.getCreatedDate() == null) {
          lead.setCreatedDate(new Date());
        }
        if (lead.getUpdatedDate() == null) {
          lead.setUpdatedDate(new Date());
        }
        if (lead.getStatus() == null) {
          lead.setStatus(LEAD_DEFAULT_STATUS);
          if (leadInfo.getResponse() != null && StringUtils.isNoneEmpty(settings.getAutoOpeningForms())
              && settings.getAutoOpeningForms().contains(leadInfo.getResponse().getFormName())) {
            lead.setStatus(LEAD_OPEN_STATUS);
          }
        }
        if (lead.getBlogSubscription() != null && lead.getBlogSubscription()) {
          lead.setBlogSubscriptionDate(new Date());
          lead.setCaptureMethod("Blog");
          lead.setCaptureType("Blog subscription");
        }
        if (leadInfo.getResponse() != null) {
          lead.setCaptureType(leadInfo.getResponse().getFormName());
          lead.setCaptureMethod(getCaptureMethode(leadInfo.getResponse().getFormName()));
          if (settings.getResourcesIdentifier() != null) {
            for (FieldDTO fieldDTO : leadInfo.getResponse().getFields()) {
              if (isResourceRequest(fieldDTO.getValue()))
                lead.setCaptureSourceInfo(fieldDTO.getValue());
              break;
            }
          }
        }
        lead.setPersonSource(getLeadSource(lead.getOriginalReferrer()));
        lead.setGeographiqueZone(getGeoZone(lead.getInferredCountry()));

        leadEntity = createLead(lead);
        if (broadcast) {
          listenerService.broadcast(NEW_LEAD_EVENT, leadEntity, "");
        }
      } else {
        leadEntity = mergeLead(leadEntity, lead);
        leadEntity.setUpdatedDate(new Date());
        if (leadEntity.getTaskId() == null || leadEntity.getTaskId() == 0) {
          if (leadEntity.getStatus().equals(LEAD_DEFAULT_STATUS) && settings.getAutoOpeningForms() != null
              && leadInfo.getResponse() != null
              && settings.getAutoOpeningForms().contains(leadInfo.getResponse().getFormName())) {
            TaskDto task_ = createTask(leadEntity);
            if (task_ != null) {
              leadEntity.setTaskId(task_.getId());
              leadEntity.setTaskUrl(buildTaskURL(task_));
              leadEntity.setStatus(LEAD_OPEN_STATUS);
            }
          }
        }
        leadDAO.update(leadEntity);
      }
      if (leadInfo.getResponse() != null) {
        addResponse(leadInfo.getResponse(), leadEntity);
      }
    } catch (Exception e) {
      LOG.error("An error occured when trying to synchronize lead", e);
      throw e;
    }
    return leadEntity;
  }

  public LeadEntity createLead(LeadDTO lead) {
    return leadDAO.create(toLeadEntity(lead));
  }

  public LeadEntity createLead(LeadDTO lead, String creator) throws Exception {
    LeadEntity leadEntity = toLeadEntity(lead);
    leadEntity = leadDAO.create(leadEntity);
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setFormName("Created manually");
    List<FieldDTO> fields = new ArrayList<>();
    FieldDTO fieldDTO = new FieldDTO();
    fieldDTO.setName("Created by");
    fieldDTO.setValue(creator);
    fields.add(fieldDTO);
    responseDTO.setFields(fields);
    addResponse(responseDTO, leadEntity);
    listenerService.broadcast(NEW_LEAD_EVENT, leadEntity, "");
    return leadEntity;
  }

  @ExoTransactional
  public void deleteLead(LeadEntity lead) throws Exception {
    try {
      leadDAO.delete(lead);
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  public void deleteAllLeads(List<LeadEntity> leadEntityList) throws Exception {
    LOG.info("=============== Start Deleting of {} leads ===============",leadEntityList.size());
    for (LeadEntity lead : leadEntityList) {
    try {
        if (lead.getTaskId() != null && lead.getTaskId() != 0) {
          taskService.removeTask(lead.getTaskId());
          LOG.info("Task {} related to lead {} removed", lead.getTaskId(), lead.getId());
        }
        leadDAO.delete(lead);
      LOG.info("-- Lead {} deleted successfully --", lead.getId());
    } catch (Exception e) {
      LOG.error("cannot delete leads", e);
    }
    }
    LOG.info("=============== Leads deleted successfully ===============");
  }

  public void updateLead(LeadDTO lead) throws Exception {
    try {
      lead.setUpdatedDate(new Date());
      leadDAO.update(toLeadEntity(lead));
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  public void assigneLead(Long leadId, String assignee) throws Exception {
    try {
      LeadEntity leadEntity = leadDAO.find(leadId);
      leadEntity.setUpdatedDate(new Date());
      leadEntity.setAssignee(assignee);
      leadDAO.update(leadEntity);
      if (leadEntity.getTaskId() != null && leadEntity.getTaskId() != 0) {
        TaskDto task = taskService.getTask(leadEntity.getTaskId());
        task.setAssignee(assignee);
        taskService.updateTask(task);
      }
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }
  public void resetTask(Long leadId) throws Exception {
    try {
      LeadEntity leadEntity = leadDAO.find(leadId);
      leadEntity.setTaskId(null);
      leadEntity.setTaskUrl("");
      leadEntity.setStatus("Raw");
      leadDAO.update(leadEntity);
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  public LeadEntity suspendLead(String mail, String cause) throws Exception {
    try {
      LeadEntity leadEntity = getLeadByMail(mail);
      if (leadEntity != null) {
        leadEntity.setUpdatedDate(new Date());
        leadEntity.setMarketingSuspended(true);
        leadEntity.setMarketingSuspendedCause("Unsubscribed - " + cause);
        leadEntity = leadDAO.update(leadEntity);
        return leadEntity;
      }
      return null;
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  public LeadEntity updateStatus(Long leadId, String status, String userName) throws Exception {
    try {
      LeadEntity leadEntity = leadDAO.find(leadId);
      leadEntity.setUpdatedDate(new Date());
      leadEntity.setStatus(status);
      boolean isBadStatus = Arrays.stream(LEAD_BAD_STATUSES).anyMatch(status::equals);
      boolean isCompletedStatus = Arrays.stream(LEAD_COMPLET_STATUSES).anyMatch(status::equals);
      if (leadEntity.getTaskId() != null && leadEntity.getTaskId() != 0){
        try {
          getTask(leadEntity.getTaskId());
        } catch (EntityNotFoundException e) {
          leadEntity.setTaskId(null);
          leadEntity.setTaskUrl(null);
        }
      }
      if (leadEntity.getTaskId() == null || leadEntity.getTaskId() == 0) {
        if (!isBadStatus) {
          TaskDto task_ = createTask(leadEntity);
          if (task_ != null) {
            leadEntity.setTaskId(task_.getId());
            leadEntity.setTaskUrl(buildTaskURL(task_));
          }
        }
      }
      leadEntity = leadDAO.update(leadEntity);
      if (leadEntity.getTaskId() != null && leadEntity.getTaskId() != 0) {
        updateTaskStatus(leadEntity.getTaskId(), status);
        if (isCompletedStatus) {
          completeTask(leadEntity.getTaskId());
        }
      }
      return leadEntity;
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  public static String buildTaskURL(TaskDto task) {
    if (task == null) {
      return "#";
    }

    StringBuilder urlBuilder = new StringBuilder(ResourceUtil.buildBaseURL());
    if (urlBuilder.length() <= 1) {
      return urlBuilder.toString();
    } else {
      return urlBuilder.append(TaskUtil.URL_TASK_DETAIL)
              .append(task.getId())
              .toString();
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

  public LeadsAccessList getLeads(String search,
                                  String status,
                                  String owner,
                                  String captureMethod,
                                  String from,
                                  String to,
                                  String zone,
                                  int min,
                                  int max,
                                  Boolean notassigned,
                                  String sortBy,
                                  Boolean sortDesc,
                                  int page,
                                  int limit,
                                  Boolean export) {
    int offset = (page - 1) * limit;
    List<LeadDTO> leadsList = new ArrayList<>();
    List<LeadEntity> leadsEntities = leadDAO.getLeads(search,
                                                      status,
                                                      owner,
                                                      captureMethod,
                                                      from,
                                                      to,
                                                      zone,
                                                      min,
                                                      max,
                                                      notassigned,
                                                      offset,
                                                      limit,
                                                      sortBy,
                                                      sortDesc);
    Long leadsTotalNumber = leadDAO.countLeads(search, status, owner, captureMethod, from, to, zone, min, max, notassigned);
    if (leadsEntities != null) {
      if (export != null && export) {
        for (LeadEntity leadEntity : leadsEntities) {
          if (leadEntity != null) {
            LeadDTO leadDTO = toLeadDto(leadEntity);
            if (leadDTO.getTaskId() != null && leadDTO.getTaskId() != 0) {
              try {
                TaskDto task = taskService.getTask(leadDTO.getTaskId());
                List<ChangeLogEntry> logs = taskService.getTaskLogs(leadDTO.getTaskId(),0,-1);
                leadDTO.setOpenedDate(formatter.format(task.getCreatedTime()));
                for (ChangeLogEntry log : logs) {
                  if (log.getActionName().equals("edit_status")) {
                    switch (log.getTarget()) {
                    case "Qualified":
                      leadDTO.setQualifiedDate(formatter.format(log.getCreatedTime()));
                      break;
                    case "Accepted":
                      leadDTO.setAcceptedDate(formatter.format(log.getCreatedTime()));
                      break;
                    case "Recycled":
                      leadDTO.setRecycledDate(formatter.format(log.getCreatedTime()));
                      break;
                    }
                  }
                }
              } catch (Exception e) {
                LOG.error("Cannot get Task log for lead {}", leadDTO.getId(), e);
              }
            }
            leadsList.add(leadDTO);
          }
        }
      } else {
        for (LeadEntity leadEntity : leadsEntities) {
          if (leadEntity != null) {
            leadsList.add(toLeadDto(leadEntity));
          }
        }
      }
    }

    return new LeadsAccessList(leadsList, leadsTotalNumber);
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
              responseEntity.setFilelds(fieldDAO.getFieldsByResponse(responseEntity.getId()));
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

  public JSONArray getTimeLine(long leadId) {
    JSONArray responsesList = new JSONArray();
    try {
      LeadEntity lead = getLeadbyId(leadId);
      if (lead != null) {
        if (lead.getCommunityRegistration() != null && lead.getCommunityRegistration().booleanValue()) {
          JSONObject obj = new JSONObject();
          obj.put("form", "communityRegistration");
          obj.put("communityUserName", lead.getCommunityUserName());
          obj.put("communityRegistrationMethod", lead.getCommunityRegistrationMethod());
          obj.put(CREATION_DATE_FIELD_NAME, formatter.format(lead.getCommunityRegistrationDate()));
          obj.put("time", lead.getCommunityRegistrationDate().getTime());
          JSONArray fields = new JSONArray();
          fields.put("communityUserName");
          fields.put("communityRegistrationMethod");
          obj.put("fields", fields);
          responsesList.put(obj);
        }

        if (lead.getBlogSubscription() != null && lead.getBlogSubscription().booleanValue()) {
          JSONObject obj = new JSONObject();
          obj.put("form", "blogRegistration");
          obj.put(CREATION_DATE_FIELD_NAME, formatter.format(lead.getBlogSubscriptionDate()));
          obj.put("time", lead.getBlogSubscriptionDate().getTime());
          obj.put("fields", new JSONArray());
          responsesList.put(obj);
        }

        List<ResponseEntity> responsesEntities = responseDAO.getResponsesByLead(leadId);
        if (responsesEntities != null && responsesEntities.size() > 0) {
          for (ResponseEntity responseEntity : responsesEntities) {
            if (responseEntity != null) {
              responseEntity.setFilelds(fieldDAO.getFieldsByResponse(responseEntity.getId()));
              JSONObject obj = Utils.toResponseJson(responseEntity);
              obj.put("form", responseEntity.getFormEntity().getName());
              obj.put("fields", responseEntity.getFormEntity().getFields().split(FIELDS_DELIMITER));
              obj.put("time", responseEntity.getCreatedDate().getTime());
              responsesList.put(obj);
            }
          }
        }

        if (lead.getTaskId() != null && lead.getTaskId() > 0) {
          IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
          List<ChangeLogEntry> logs = taskService.getTaskLogs(lead.getTaskId(),0,-1);
          for (ChangeLogEntry log : logs) {
            if (log.getActionName().equals("edit_status")) {
              JSONObject obj = new JSONObject();
              obj.put("form", "task");
              obj.put("author", log.getAuthor());
              obj.put("newStatus", log.getTarget());
              Identity identity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, log.getAuthor());
              if (identity != null) {
                obj.put("authorName", identity.getProfile().getFullName());
              } else {
                obj.put("authorName", log.getAuthor());
              }
              obj.put(CREATION_DATE_FIELD_NAME, formatter.format(log.getCreatedTime()));
              obj.put("time", log.getCreatedTime());
              obj.put("fields", new JSONArray());
              responsesList.put(obj);
            }
          }

        }

      }
    } catch (Exception e) {
      LOG.error("Cannot get responses for lead {}", leadId, e);
    }

    return responsesList;
  }

  public void addResponse(ResponseDTO responseDTO, LeadEntity leadEntity) throws Exception {

    try {
      FormEntity formEntity = formDAO.getFormByName(responseDTO.getFormName());
      if (formEntity == null) {
        String fields = responseDTO.getFields().stream().map(n -> n.getName()).collect(Collectors.joining(","));
        if (!fields.contains(CREATION_DATE_FIELD_NAME)) {
          fields = (fields.concat("," + CREATION_DATE_FIELD_NAME));
        }
        formEntity = createForm(new FormEntity(responseDTO.getFormName(), fields));
      } else {
        String fields = formEntity.getFields();
        List<String> fieldList = new ArrayList<String>(Arrays.asList(fields.split(FIELDS_DELIMITER)));
        if (!fieldList.contains(CREATION_DATE_FIELD_NAME)) {
          fieldList.add(CREATION_DATE_FIELD_NAME);
        }
        boolean changed = false;
        for (FieldDTO field : responseDTO.getFields()) {
          if (!fieldList.contains(field.getName())) {
            fieldList.add(field.getName());
            changed = true;
          }
        }
        if (changed) {
          fields = fieldList.stream().collect(Collectors.joining(FIELDS_DELIMITER));
          formEntity.setFields(fields);
          updateForm(formEntity);
        }
      }
      ResponseEntity responseEntity = new ResponseEntity(formEntity, leadEntity);
      if (responseDTO.getCreatedDate() != null) {
        responseEntity.setCreatedDate(responseDTO.getCreatedDate());
      }
      responseEntity = createResponse(responseEntity);

      for (FieldDTO field : responseDTO.getFields()) {
        FieldEntity fieldEntity = new FieldEntity(field.getName(), field.getValue(), responseEntity);
        fieldDAO.create(fieldEntity);
      }
      listenerService.broadcast(NEW_RESPONSE_EVENT, leadEntity, responseEntity);
    } catch (Exception e) {
      LOG.error("An error occured when trying to add response", e);
      throw e;
    }
  }

  public List<ResponseEntity> getAllResponses() {
    return responseDAO.findAll();
  }

  public FormEntity createForm(FormEntity formEntity) {

    return formDAO.create(formEntity);
  }

  public FormEntity updateForm(FormEntity formEntity) {

    return formDAO.update(formEntity);
  }

  public ResponseEntity createResponse(ResponseEntity responseEntity) {
    if (responseEntity.getCreatedDate() == null) {
      responseEntity.setCreatedDate(new Date());
    }
    return responseDAO.create(responseEntity);
  }

  public LeadEntity getLeadbyId(long id) {
    return leadDAO.find(id);
  }

  public LeadEntity getLeadByMail(String mail) {
    return leadDAO.getLeadByMail(mail);
  }

  public LeadEntity getLeadByTask(Long taslId) {
    return leadDAO.getLeadByTask(taslId);
  }

  public List<LeadEntity> getLeadsByStatus(String status) {
    return leadDAO.getLeadsByStatus(status);
  }

  public JSONArray getTaskComments(long taskId) {
    return Utils.getCommentsJson(commentService.getComments(taskId,0,-1));
  }

  public TaskDto getTask(long taskId) throws Exception {
    try {
      return taskService.getTask(taskId);
    } catch (EntityNotFoundException e) {
      LOG.error("Cannot get Task", e);
      throw e;
    }
  }

  public JSONObject addTaskComment(long taskId, String username, String comment) throws Exception {
    try {
      CommentDto comment_ = commentService.addComment(taskService.getTask(taskId), username, comment);
      OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
      return Utils.commentToJson(comment_,
                                 comment_.getAuthor(),
                                 organizationService.getUserHandler().findUserByName(comment_.getAuthor()).getDisplayName());
    } catch (EntityNotFoundException enf) {
      LOG.error("Cannot Add Comment", enf);
      throw enf;
    } catch (Exception e) {
      LOG.error("Cannot conevert comment to json", e);
      throw e;
    }
  }

  public void updateTaskStatus(Long taskId, String status) throws Exception {
    try {
      LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
      TaskDto task = taskService.getTask(taskId);
      SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
      Space uxSpace = spaceService.getSpaceByPrettyName(settings.getUserExperienceSpace());
      List<StatusDto> statuses = statusService.getStatuses(Utils.getTaskProject(uxSpace.getGroupId(), settings.getLeadTaskProject())
                                                             .getId());
      StatusDto newStatus = null;
      for (StatusDto status_ : statuses) {
        if (status_.getName().equals(status)) {
          newStatus = status_;
        }
      }
      if (newStatus != null) {
        task.setStatus(newStatus);
        taskService.updateTask(task);
      }
    } catch (Exception e) {
      LOG.error("Cannot update Task status", e);
      throw e;
    }
  }

  public TaskDto createTask(LeadEntity lead) throws Exception {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    Space uxSpace = spaceService.getSpaceByPrettyName(settings.getUserExperienceSpace());
    if (uxSpace != null) {
      StatusDto status = statusService.getDefaultStatus(Utils.getTaskProject(uxSpace.getGroupId(), settings.getLeadTaskProject())
                                                          .getId());
      TaskDto task = new TaskDto();
      task.setTitle(lead.getMail());
      if (StringUtils.isNoneEmpty(lead.getFirstName()) && StringUtils.isNoneEmpty(lead.getLastName())) {
        task.setTitle(lead.getFirstName() + " " + lead.getLastName());
      }
      task.setDescription("<a  href=\"" + leadCaptureSettingsService.getSettings().getLeadManagementAppUrl() + "?leadid="
          + lead.getId() + "\">" + lead.getFirstName() + " " + lead.getLastName() + " </a>");
      task.setStatus(status);
      task.setPriority(Priority.NORMAL);
      task.setCreatedBy(settings.getUserExperienceBotUserName());
      task.setCreatedTime(new Date());
      task = taskService.createTask(task);
      return task;
    }
    return null;
  }

  public void completeTask(Long id) throws Exception {
    TaskDto task = taskService.getTask(id);
    task.setCompleted(true);
    taskService.updateTask(task);
  }

  public List<PersonalTask> getPersonalTasks(long id, String userId) throws Exception {
    try {
      LeadEntity leadEntity = getLeadbyId(id);
      List<PersonalTask> pTasks = new ArrayList<>();
      if (leadEntity.getTasksLabelId() != null && leadEntity.getTasksLabelId() > 0) {
        List<TaskDto> tasks = taskService.findTasksByLabel(labelService.getLabel(leadEntity.getTasksLabelId()), null, userId, null,0,-1);
        for (TaskDto task : tasks) {
          PersonalTask pTask =
                             new PersonalTask(task.getId(), null, userId, task.getTitle(), task.getDueDate(), task.isCompleted());
          pTasks.add(pTask);
        }
      }
      return pTasks;
    } catch (Exception e) {
      LOG.error("Cannot get personal Tasks", e);
      throw e;
    }
  }

  public PersonalTask createPersonalTask(PersonalTask personalTask) throws Exception {
    TaskDto task = new TaskDto();
    LeadDTO lead = personalTask.getLead();
    String title = personalTask.getTitle();
    String userId = personalTask.getUserId();
    Date dueDate = personalTask.getDueDate();
    task.setTitle(lead.getMail());
    task.setTitle(title);
    task.setDescription("<a  href=\"" + leadCaptureSettingsService.getSettings().getLeadManagementAppUrl() + "?leadid="
        + lead.getId() + "\">" + lead.getFirstName() + " " + lead.getLastName() + " </a>");
    task.setCreatedBy(userId);
    task.setCreatedTime(new Date());
    task.setAssignee(userId);
    task.setDueDate(dueDate);
    task = taskService.createTask(task);
    LabelDto label = null;
    if (lead.getTasksLabelId() != null) {
      label = labelService.getLabel(lead.getTasksLabelId());
    }
    if (label == null) {
      List<LabelDto> labels = labelService.findLabelsByUser(userId,0,-1);
      for (LabelDto label_ : labels) {
        if (label_.getName().equals(lead.getFirstName() + " " + lead.getLastName())) {
          label = label_;
          break;
        }
      }
    }
    if (label == null) {
      label = new LabelDto();
      label.setName(lead.getFirstName() + " " + lead.getLastName());
      label.setUsername(userId);
      label = labelService.createLabel(label);
      lead.setTasksLabelId(label.getId());
      updateLead(lead);
    }
    labelService.addTaskToLabel(task, label.getId());
    return new PersonalTask(task.getId(), null, userId, task.getTitle(), task.getDueDate(), task.isCompleted());
  }

  public TaskDto updatePersonalTask(PersonalTask pTask) throws Exception {
    TaskDto task = taskService.getTask(pTask.getId());
    if (task != null) {
      task.setCompleted(pTask.isCompleted());
      taskService.updateTask(task);
    }
    return task;
  }

  public LeadEntity mergeLead(LeadEntity leadEntity, LeadDTO leadDTO) {

    if (!StringUtils.isEmpty(leadDTO.getFirstName()))
      leadEntity.setFirstName(leadDTO.getFirstName());
    if (!StringUtils.isEmpty(leadDTO.getLastName()))
      leadEntity.setLastName(leadDTO.getLastName());
    if (!StringUtils.isEmpty(leadDTO.getCompany()))
      leadEntity.setCompany(leadDTO.getCompany());
    if (!StringUtils.isEmpty(leadDTO.getPosition()))
      leadEntity.setPosition(leadDTO.getPosition());
    if (!StringUtils.isEmpty(leadDTO.getDepartment()))
      leadEntity.setDepartment(leadDTO.getDepartment());
    if (!StringUtils.isEmpty(leadDTO.getCompanySize()))
      leadEntity.setCompanySize(leadDTO.getCompanySize());

    if (!StringUtils.isEmpty(leadDTO.getInferredCountry())) {
      if (StringUtils.isEmpty(leadEntity.getGeographiqueZone())
          || !leadDTO.getInferredCountry().equals(leadEntity.getCountry())) {
        leadDTO.setGeographiqueZone(getGeoZone(leadDTO.getInferredCountry()));
        leadEntity.setGeographiqueZone(leadDTO.getGeographiqueZone());
        leadEntity.setCountry(leadDTO.getInferredCountry());
      }
    }
    if (!StringUtils.isEmpty(leadDTO.getPhone()))
      leadEntity.setPhone(leadDTO.getPhone());
    if (!StringUtils.isEmpty(leadDTO.getLanguage()))
      leadEntity.setLanguage(leadDTO.getLanguage());
    if (!StringUtils.isEmpty(leadDTO.getAssignee()))
      leadEntity.setAssignee(leadDTO.getAssignee());
    if (!StringUtils.isEmpty(leadDTO.getCaptureMethod()))
      leadEntity.setCaptureMethod(leadDTO.getCaptureMethod());
    if (!StringUtils.isEmpty(leadDTO.getCaptureType()))
      leadEntity.setCaptureType(leadDTO.getCaptureType());
    if ((leadEntity.getBlogSubscription() == null || !leadEntity.getBlogSubscription())
        && leadDTO.getBlogSubscription() != null) {
      leadEntity.setBlogSubscription(true);
      leadEntity.setBlogSubscriptionDate(new Date());
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

  public JSONObject toFormJson(FormEntity formEntity) throws JSONException {
    JSONObject formJson = new JSONObject();
    formJson.put("id", formEntity.getId());
    formJson.put("name", formEntity.getName());
    formJson.put("fields", formEntity.getFields().split(FIELDS_DELIMITER));
    return formJson;
  }

  public LeadDTO toLeadDto(LeadEntity leadEntity) {
    LeadDTO leadDTO = new LeadDTO();
    leadDTO.setId(leadEntity.getId());
    leadDTO.setMail(leadEntity.getMail());
    leadDTO.setFirstName(leadEntity.getFirstName());
    leadDTO.setLastName(leadEntity.getLastName());
    leadDTO.setCompany(leadEntity.getCompany());
    leadDTO.setPosition(leadEntity.getPosition());
    leadDTO.setInferredCountry(leadEntity.getCountry());
    leadDTO.setStatus(leadEntity.getStatus());
    leadDTO.setPhone(leadEntity.getPhone());
    if (leadEntity.getCreatedDate() != null) {
      leadDTO.setCreatedDate(leadEntity.getCreatedDate());
      leadDTO.setFormattedCreatedDate(formatter.format(leadEntity.getCreatedDate()));
    }
    if (leadEntity.getUpdatedDate() != null) {
      leadDTO.setUpdatedDate(leadEntity.getUpdatedDate());
      leadDTO.setFormattedUpdatedDate(formatter.format(leadEntity.getUpdatedDate()));
    }
    leadDTO.setLanguage(leadEntity.getLanguage());
    leadDTO.setAssignee(leadEntity.getAssignee());
    leadDTO.setGeographiqueZone(leadEntity.getGeographiqueZone());
    leadDTO.setMarketingSuspended(leadEntity.getMarketingSuspended());
    leadDTO.setMarketingSuspendedCause(leadEntity.getMarketingSuspendedCause());
    leadDTO.setCaptureMethod(leadEntity.getCaptureMethod());
    leadDTO.setCaptureType(leadEntity.getCaptureType());
    leadDTO.setBlogSubscription(leadEntity.getBlogSubscription());
    leadDTO.setBlogSubscriptionDate(leadEntity.getBlogSubscriptionDate());
    if (leadEntity.getBlogSubscriptionDate() != null)
      leadDTO.setFormattedBlogSubscriptionDate(formatter.format(leadEntity.getBlogSubscriptionDate()));
    leadDTO.setCommunityUserName(leadEntity.getCommunityUserName());
    leadDTO.setCommunityRegistration(leadEntity.getCommunityRegistration());
    leadDTO.setCommunityRegistrationMethod(leadEntity.getCommunityRegistrationMethod());
    leadDTO.setCommunityRegistrationDate(leadEntity.getCommunityRegistrationDate());
    if (leadEntity.getCommunityRegistrationDate() != null)
      leadDTO.setFormattedCommunityRegistrationDate(formatter.format(leadEntity.getCommunityRegistrationDate()));
    leadDTO.setPersonSource(leadEntity.getPersonSource());
    leadDTO.setLandingPageInfo(leadEntity.getLandingPageInfo());
    leadDTO.setCaptureSourceInfo(leadEntity.getCaptureSourceInfo());
    leadDTO.setPersonIp(leadEntity.getPersonIp());
    leadDTO.setOriginalReferrer(leadEntity.getOriginalReferrer());
    leadDTO.setTaskId(leadEntity.getTaskId());
    leadDTO.setTaskUrl(leadEntity.getTaskUrl());
    leadDTO.setActivityId(leadEntity.getActivityId());
    leadDTO.setGoal(leadEntity.getGoal());
    leadDTO.setUsersNumber(leadEntity.getUsersNumber());
    leadDTO.setInteractionSummary(leadEntity.getInteractionSummary());
    leadDTO.setCurrentSolution(leadEntity.getCurrentSolution());
    leadDTO.setHowHear(leadEntity.getHowHear());
    leadDTO.setSolutionType(leadEntity.getSolutionType());
    leadDTO.setSolutionRequirements(leadEntity.getSolutionRequirements());
    leadDTO.setShortlistVendors(leadEntity.getShortlistVendors());
    leadDTO.setCompanyWebsite(leadEntity.getCompanyWebsite());
    leadDTO.setEmployeesNumber(leadEntity.getEmployeesNumber());
    leadDTO.setIndustry(leadEntity.getIndustry());
    leadDTO.setTasksLabelId(leadEntity.getTasksLabelId());
    leadDTO.setDepartment(leadEntity.getDepartment());
    leadDTO.setCompanySize(leadEntity.getCompanySize());
    leadDTO.setCustomer(leadEntity.getCustomer()!=null&&leadEntity.getCustomer()?true:false);
    return leadDTO;
  }

  public LeadEntity toLeadEntity(LeadDTO leadDTO) {
    LeadEntity leadEntity = new LeadEntity();
    leadEntity.setId(leadDTO.getId());
    leadEntity.setMail(leadDTO.getMail());
    leadEntity.setFirstName(leadDTO.getFirstName());
    leadEntity.setLastName(leadDTO.getLastName());
    leadEntity.setCompany(leadDTO.getCompany());
    leadEntity.setPosition(leadDTO.getPosition());
    leadEntity.setCountry(leadDTO.getInferredCountry());
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
    leadEntity.setTaskId(leadDTO.getTaskId());
    leadEntity.setTaskUrl(leadDTO.getTaskUrl());
    leadEntity.setActivityId(leadDTO.getActivityId());
    leadEntity.setGoal(leadDTO.getGoal());
    leadEntity.setUsersNumber(leadDTO.getUsersNumber());
    leadEntity.setInteractionSummary(leadDTO.getInteractionSummary());
    leadEntity.setCurrentSolution(leadDTO.getCurrentSolution());
    leadEntity.setHowHear(leadDTO.getHowHear());
    leadEntity.setSolutionType(leadDTO.getSolutionType());
    leadEntity.setSolutionRequirements(leadDTO.getSolutionRequirements());
    leadEntity.setShortlistVendors(leadDTO.getShortlistVendors());
    leadEntity.setCompanyWebsite(leadDTO.getCompanyWebsite());
    leadEntity.setEmployeesNumber(leadDTO.getEmployeesNumber());
    leadEntity.setIndustry(leadDTO.getIndustry());
    leadEntity.setTasksLabelId(leadDTO.getTasksLabelId());
    leadEntity.setDepartment(leadDTO.getDepartment());
    leadEntity.setCompanySize(leadDTO.getCompanySize());
    leadEntity.setCustomer(leadDTO.getCustomer());
    return leadEntity;
  }

  public FieldDTO toFieldDto(FieldEntity fieldEntity) {
    FieldDTO fieldDTO = new FieldDTO();
    fieldDTO.setId(fieldEntity.getId());
    fieldDTO.setName(fieldEntity.getName());
    fieldDTO.setValue(fieldEntity.getValue());
    return fieldDTO;
  }

  public FieldEntity toFieldEntity(FieldDTO fieldDTO) {
    FieldEntity fieldEntity = new FieldEntity();
    fieldEntity.setId(fieldDTO.getId());
    fieldEntity.setName(fieldDTO.getName());
    fieldEntity.setValue(fieldDTO.getValue());
    return fieldEntity;
  }

  public FormDTO toFormDto(FormEntity formEntity) {
    FormDTO formDTO = new FormDTO();
    formDTO.setId(formEntity.getId());
    formDTO.setName(formEntity.getName());
    formDTO.setFields(formEntity.getFields());
    return formDTO;
  }

  public FormEntity toFormEntity(FormDTO formDTO) {
    FormEntity formEntity = new FormEntity();
    formEntity.setId(formDTO.getId());
    formEntity.setName(formDTO.getName());
    formEntity.setFields(formDTO.getFields());
    return formEntity;
  }

  public ResponseDTO toResponseDto(ResponseEntity responseEntity) {
    ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setId(responseEntity.getId());
    responseDTO.setCreatedDate(responseEntity.getCreatedDate());
    responseDTO.setForm(toFormDto(responseEntity.getFormEntity()));
    responseDTO.setFormName(responseEntity.getFormEntity().getName());
    List<FieldDTO> fields = new ArrayList<>();
    for (FieldEntity fieald : fieldDAO.getFieldsByResponse(responseEntity.getId())) {
      fields.add(toFieldDto(fieald));
    }
    responseDTO.setFields(fields);
    return responseDTO;
  }

  public ResponseEntity toResponseEntity(ResponseDTO responseDTO) {
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

  public boolean updateMethodes() throws Exception {
    try {
      List<LeadEntity> leadsEntities = leadDAO.findAll();
      for (LeadEntity lead : leadsEntities) {
        if (lead.getCaptureMethod() != null && !lead.getCaptureMethod().equals("Blog")) {
          if (lead.getCaptureMethod().equals("Register form") || lead.getCaptureMethod().contains("community")) {
            lead.setCaptureType(lead.getCaptureMethod());
            lead.setCaptureMethod("Community registration");
          } else {
            if (!getCaptureMethode(lead.getCaptureMethod()).equals(lead.getCaptureMethod())) {
              lead.setCaptureMethod(getCaptureMethode(lead.getCaptureMethod()));
              lead.setCaptureSourceInfo(lead.getCaptureType());
              lead.setCaptureType(lead.getCaptureMethod());
            }
          }
        }
      }
      leadDAO.updateAll(leadsEntities);
      return true;
    } catch (Exception e) {
      throw (e);
    }
  }

  public boolean mergezone() throws Exception {
    try {
      List<LeadEntity> leadsEntities = leadDAO.findAll();
      List<LeadEntity> leadsEntitiestoUpdate = new ArrayList<>();
      for (LeadEntity lead : leadsEntities) {
        if (lead.getGeographiqueZone() != null && (lead.getGeographiqueZone().equals(LC_G_ZONE_WESTERN_EUROPE_NAME)
            || lead.getGeographiqueZone().equals(LC_G_ZONE_ESTERN_EUROPE_NAME))) {
          lead.setGeographiqueZone(LC_G_ZONE_EUROPE_NAME);
          leadsEntitiestoUpdate.add(lead);
        }
      }
      leadDAO.updateAll(leadsEntitiestoUpdate);
      return true;
    } catch (Exception e) {
      throw (e);
    }
  }
}
