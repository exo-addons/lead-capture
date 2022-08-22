package org.exoplatform.leadcapture.rest;

import static org.exoplatform.leadcapture.Utils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.task.exception.EntityNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.dto.PersonalTask;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.Util;

import org.exoplatform.services.rest.http.PATCH;

@Path("/leadcapture/leadsmanagement")
@Produces(MediaType.APPLICATION_JSON)

public class LeadsManagementRest implements ResourceContainer {

  private final Log                  LOG                 = ExoLogger.getLogger(LeadsManagementRest.class);

  private final String               portalContainerName = "portal";

  private LeadsManagementService     leadsManagementService;

  private LeadCaptureSettingsService leadCaptureSettingsService;

  public LeadsManagementRest(LeadsManagementService leadsManagementService,
                             LeadCaptureSettingsService leadCaptureSettingsService) {
    this.leadsManagementService = leadsManagementService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  @OPTIONS
  @Path("leads")
  public Response allowCORS(@Context UriInfo uriInfo) throws Exception {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    Response.ResponseBuilder response = Response.ok();
    response.header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain());
    response.header("Access-Control-Allow-Headers", "*");
    response.header("Access-Control-Request-Method", "POST");
    return response.build();
  }

  @GET
  @Path("leads")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getLeads(@Context UriInfo uriInfo,
                           @QueryParam("search") String search,
                           @QueryParam("status") String status,
                           @QueryParam("owner") String owner,
                           @QueryParam("method") String captureMethod,
                           @QueryParam("from") String from,
                           @QueryParam("to") String to,
                           @QueryParam("zone") String zone,
                           @QueryParam("min") int min,
                           @QueryParam("max") int max,
                           @QueryParam("notassigned") Boolean notassigned,
                           @QueryParam("sortby") String sortBy,
                           @QueryParam("sortdesc") Boolean sortDesc,
                           @QueryParam("page") int page,
                           @QueryParam("limit") int limit,
                           @QueryParam("export") Boolean export) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {

      return Response.ok(leadsManagementService.getLeads(search, status, owner, captureMethod, from, to, zone, min, max, notassigned, sortBy, sortDesc, page, limit, export)).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get leads list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("leads/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getLead(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.toLeadDto(leadsManagementService.getLeadbyId(id))).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get leads list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("leads")
  public Response add(@Context UriInfo uriInfo, @HeaderParam("token") String headerToken, FormInfo lead) throws Exception {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();

    // String captureToken = System.getProperty(LEAD_CAPTURE_TOKEN);
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    String captureToken = settings.getCaptureToken();
    if (headerToken == null) {
      LOG.warn("Security Token for Lead capture not defined");
      return Response.status(Response.Status.FORBIDDEN)
                     .entity("Access forbidden to the add lead rest service, Security Token not defined")
                     .build();
    }
    if (StringUtils.isEmpty(captureToken) || !captureToken.equals(headerToken)) {
      LOG.warn("Access forbidden to the add lead rest service, wrong token: {}", headerToken);
      return Response.status(Response.Status.FORBIDDEN)
                     .entity("Access forbidden to the add lead rest service, wrong token")
                     .build();
    }

    if (lead == null) {
      LOG.warn("Lead not captured, lead is null");
      return Response.status(Response.Status.BAD_REQUEST).entity("Lead is null").build();
    }
    if (lead.getLead() == null || StringUtils.isEmpty(lead.getLead().getMail())) {
      LOG.warn("Lead not captured, mail needed");
      return Response.status(Response.Status.BAD_REQUEST).entity("Lead mail needed").build();
    }
    if (isBlacklisted(lead.getLead())) {
      LOG.warn("Cannot capture lead {} with mail  {}, lead  blacklisted",
               lead.getLead().getFirstName() + " " + lead.getLead().getLastName(), lead.getLead().getMail());
      return Response.status(Response.Status.UNAUTHORIZED).entity("lead blacklisted").build();
    }

    if (!leadCaptureSettingsService.getSettings().isCaptureEnabled()) {
      LOG.warn("Lead capture not enabled, New lead {} not captured ",
               lead.getLead().getFirstName() + " " + lead.getLead().getLastName());
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    LOG.info("start adding lead {}", lead.toString());
    try {

      leadsManagementService.addLeadInfo(lead, true);
      LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_name:{},form_name:{}\"",
               lead.getLead().getFirstName() + " " + lead.getLead().getLastName(),
               lead.getResponse() != null ? lead.getResponse().getFormName() : "");

      return Response.status(Response.Status.OK)
                     .entity("lead synchronized")
                     .header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain())
                     .build();

    } catch (Exception e) {
      LOG.error("An error occured when trying to synchronise lead {}",
                lead.getLead().getFirstName() + " " + lead.getLead().getLastName(),
                e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain())
                     .entity(e.getMessage())
                     .build();
    }
  }

  @DELETE
  @Path("leads/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response deleteLead(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      LeadEntity lead = leadsManagementService.getLeadbyId(id);
      if (lead == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Lead Not found").build();
      }
      leadsManagementService.deleteLead(lead);
      LOG.info("Lead {} deleted by {}", lead.getId(), sourceIdentity.getRemoteId());
      return Response.status(Response.Status.OK).entity("lead deleted").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to delete lead {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @DELETE
  @Path("leads/bulkdelete")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response deleteLeads(@Context UriInfo uriInfo,  List<Long> ids) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
   try {
          List<LeadEntity> leadEntityList = new ArrayList<>();
          LeadEntity lead = null;
          for (long id : ids) {
            lead = leadsManagementService.getLeadbyId(id);
            if (lead == null) {
              LOG.warn("Lead {} Not found", id);
            } else {
              leadEntityList.add(lead);
            }
          }
          leadsManagementService.deleteAllLeads(leadEntityList);
          return Response.status(Response.Status.OK).entity("leads deleted").build();
      } catch (Exception e) {
      LOG.error("An error occured when trying to delete leads ", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("leads/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response update(@Context UriInfo uriInfo, @PathParam("id") Long id, LeadDTO lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      leadsManagementService.updateLead(lead);
      LOG.info("Lead {} edited by {}", lead.getId(), sourceIdentity.getRemoteId());
      return Response.status(Response.Status.OK).entity("lead updated").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update lead {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("suspend")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response suspend(@Context UriInfo uriInfo,
                          Map<String, String> obj) throws Exception {
    try {
      String mail = obj.get("mail");
      String cause = obj.get("cause");
      LeadEntity lead = leadsManagementService.suspendLead(mail, cause);
      if(lead==null){
        LOG.warn("Lead not found");
        return Response.status(Response.Status.NOT_FOUND).entity("Lead Not found").build();
      }
      LOG.info("Lead  suspended");
      return Response.status(Response.Status.OK)
              .entity("lead suspended")
              .build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to suspend lead ", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .build();
    }
  }

  @PATCH
  @Path("assign")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response assign(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      leadsManagementService.assigneLead(lead.getId(), lead.getAssignee());
      LOG.info("Lead {} assigned to {} by {}", lead.getId(), lead.getAssignee(), sourceIdentity.getRemoteId());
      return Response.status(Response.Status.OK).entity("lead assigned").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to assign lead {} to {}", lead.getId(), lead.getAssignee(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PATCH
  @Path("status")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response updateStatus(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      LeadEntity leadEntity = leadsManagementService.updateStatus(lead.getId(), lead.getStatus(), sourceIdentity.getRemoteId());
      LOG.info("Lead {} status updated to {} by {}", lead.getId(), lead.getStatus(), sourceIdentity.getRemoteId());
      return Response.status(Response.Status.OK).entity(leadsManagementService.toLeadDto(leadEntity)).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update lead {} status to {}", lead.getId(), lead.getStatus(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PATCH
  @Path("reset/task/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response resetTask(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      LeadEntity leadEntity = leadsManagementService.getLeadbyId(id);
      if(leadEntity == null){
        return Response.status(Response.Status.BAD_REQUEST).entity("lead not found").build();
      }
      leadsManagementService.resetTask(id);
      LOG.info("Lead {} task reset by {}",  leadEntity.getTaskId(), sourceIdentity.getRemoteId());
      return Response.status(Response.Status.OK).entity(leadsManagementService.toLeadDto(leadEntity)).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to reset Task", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("responses/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getResponses(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.getResponses(id).toString()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get responses for lead {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }
  @GET
  @Path("timeline/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getTimeLine(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.getTimeLine(id).toString()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get responses for lead {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("ptask")
  @RolesAllowed("ux-team")
  public Response addPersonalTask(@Context UriInfo uriInfo, PersonalTask task) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      task.setUserId(sourceIdentity.getRemoteId());
      task.setDueDate(taskFormatter.parse(task.getFormattedDueDate()));
      return Response.ok(leadsManagementService.createPersonalTask(task)).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to add personal task", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("ptask/{id}")
  @RolesAllowed("ux-team")
  public Response updateTask(@Context UriInfo uriInfo, @PathParam("id") Long id, PersonalTask task) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.updatePersonalTask(task)).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update personal task", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("ptask/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getPersonalTasks(@Context UriInfo uriInfo, @PathParam("id") long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.getPersonalTasks(id, sourceIdentity.getRemoteId())).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get user  tasks", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @POST
  @Path("comments/{taskid}")
  @RolesAllowed("ux-team")
  public Response addComment(@Context UriInfo uriInfo, @PathParam("taskid") long taskId, String comment) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.addTaskComment(taskId, sourceIdentity.getRemoteId(), comment).toString()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to add comment to task {}", taskId, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("comments/{taskid}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getComments(@Context UriInfo uriInfo, @PathParam("taskid") long taskId) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.getTaskComments(taskId).toString()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get task {} comments", taskId, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("task/{taskid}")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getTask(@Context UriInfo uriInfo, @PathParam("taskid") long taskId) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.getTask(taskId)).build();
    } catch (EntityNotFoundException e) {
      LOG.error("task {} not found", taskId, e);
      return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
    }catch (Exception e) {
      LOG.error("An error occured when trying to get task {}", taskId, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @GET
  @Path("marketers")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getMarketers(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      JSONArray marketersList = new JSONArray();
      for (User user : Utils.getGroupMembers(leadCaptureSettingsService.getSettings().getUserExperienceGroup())) {
        JSONObject marketer = new JSONObject();
        marketer.put("userName", user.getUserName());
        marketer.put("fullName", user.getFirstName() + " " + user.getLastName());
        marketer.put("email", user.getEmail());
        marketersList.put(marketer);
      }
      return Response.ok(marketersList.toString()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get marketers list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  public boolean isBlacklisted(LeadDTO lead) {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    String mailBlackList = settings.getMailsBlackList();
    String firstNameBlackList = settings.getFirstNamesBlackList();
    String lastNameBlackList = settings.getLaslNamesBlackList();
    String companyBlackList = settings.getCompaniesBlackList();
    if (StringUtils.isNotEmpty(mailBlackList)) {
      for (String mail_ : mailBlackList.split(FIELDS_DELIMITER)) {
        if (lead.getMail().contains(mail_)) {
          return true;
        }
      }
    }
    if (StringUtils.isNotEmpty(firstNameBlackList)) {
      for (String fName : firstNameBlackList.split(FIELDS_DELIMITER)) {
        if (lead.getFirstName().equals(fName)) {
          return true;
        }
      }
    }
    if (StringUtils.isNotEmpty(lastNameBlackList)) {
      for (String lName : lastNameBlackList.split(FIELDS_DELIMITER)) {
        if (lead.getLastName().equals(lName)) {
          return true;
        }
      }
    }
    if (StringUtils.isNotEmpty(companyBlackList)) {
      for (String company : companyBlackList.split(FIELDS_DELIMITER)) {
        if (lead.getCompany().equals(company)) {
          return true;
        }
      }
    }
    return false;
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  @Path("leads/import")
  public Response importLeads(@Context UriInfo uriInfo, List<FormInfo> leads) throws Exception {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    if (!leadCaptureSettingsService.getSettings().isCaptureEnabled()) {
      LOG.warn("Leads capture not enabled ");
      return Response.status(Response.Status.NOT_MODIFIED).build();
    }

    if (leads == null) {
      LOG.warn("Leads not imported, leads is null");
      return Response.status(Response.Status.BAD_REQUEST).entity("Lead is null").build();
    }
    try {
      for (FormInfo lead : leads) {
        if (lead.getLead() == null || StringUtils.isEmpty(lead.getLead().getMail())) {
          LOG.warn("Lead not captured, mail needed");
          continue;
        }
        if (isBlacklisted(lead.getLead())) {
          LOG.warn("Cannot capture lead {} with mail  {}, lead  blacklisted",
                  lead.getLead().getFirstName() + " " + lead.getLead().getLastName(), lead.getLead().getMail());
          continue;
        }
        LOG.info("start adding lead {}", lead.getLead().getFirstName() + " " + lead.getLead().getFirstName());
        lead.getLead().setId(null);
        leadsManagementService.addLeadInfo(lead, true);
        LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_name:{},form_name:{}\"",
                 lead.getLead().getFirstName() + " " + lead.getLead().getLastName(),
                 lead.getResponse() != null ? lead.getResponse().getFormName() : "");
      }
      return Response.status(Response.Status.OK).entity("leads imported").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to import leads", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                     .header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain())
                     .entity(e.getMessage())
                     .build();
    }
  }

  @GET
  @Path("leads/export")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  public Response exportLeads(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      List<FormInfo> leadsList = new ArrayList<>();
      List<ResponseEntity> responseEntities = leadsManagementService.getAllResponses();
      for (ResponseEntity response : responseEntities) {
        FormInfo formInfo = new FormInfo();
        LeadDTO leadDTO = leadsManagementService.toLeadDto(response.getLeadEntity());
        leadDTO.setId(null);
        leadDTO.setTaskUrl(null);
        leadDTO.setTaskId(null);
        formInfo.setLead(leadDTO);
        formInfo.setResponse(leadsManagementService.toResponseDto(response));
        leadsList.add(formInfo);
      }
      List<LeadDTO> leadDTOS = leadsManagementService.getLeads();
      for (LeadDTO leadDTO : leadDTOS) {
        if (leadsManagementService.getResponses(leadDTO.getId()).length() == 0) {
          FormInfo formInfo = new FormInfo();
          leadDTO.setId(null);
          leadDTO.setTaskUrl(null);
          leadDTO.setTaskId(null);
          formInfo.setLead(leadDTO);
          leadsList.add(formInfo);
        }
      }
      return Response.ok(leadsList).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get leads list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  @Path("create")
  public Response createLead(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {

    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    if (lead == null || StringUtils.isEmpty(lead.getMail())) {
      LOG.warn("Lead not captured, mail needed");
      return Response.status(Response.Status.BAD_REQUEST).entity("Lead mail needed").build();
    }

    LOG.info("start adding lead {}", lead.toString());
    try {
      lead.setCreatedDate(new Date());
      lead.setUpdatedDate(new Date());
      lead.setStatus(LEAD_DEFAULT_STATUS);
      lead.setCaptureMethod("manually_created");
      leadsManagementService.createLead(lead);
      LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_name:{}\"",
               lead.getFirstName() + " " + lead.getLastName());

      return Response.status(Response.Status.OK).entity("lead added").build();

    } catch (Exception e) {
      LOG.error("An error occured when trying to synchronise lead {}", lead.getFirstName() + " " + lead.getLastName(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @POST
  @RolesAllowed("administrators")
  @Path("updatemethodes")
  public Response updateMethodes(@Context UriInfo uriInfo) throws Exception {

    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    try {
      leadsManagementService.updateMethodes();
      return Response.status(Response.Status.OK).entity("lead's methodes updated").build();

    } catch (Exception e) {
      LOG.error("An error occured when trying to update methodes lead",  e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }
  @POST
  @RolesAllowed("administrators")
  @Path("mergezone")
  public Response mergeZone(@Context UriInfo uriInfo) throws Exception {

    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    try {
      leadsManagementService.mergezone();
      return Response.status(Response.Status.OK).entity("leads geo zone updated").build();

    } catch (Exception e) {
      LOG.error("An error occured when trying to update geo zone of leads",  e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

}
