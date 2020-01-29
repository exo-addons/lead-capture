package org.exoplatform.leadcapture.rest;

import static org.exoplatform.leadcapture.Utils.*;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.Util;

import io.swagger.jaxrs.PATCH;

import static org.exoplatform.leadcapture.Utils.ALLOWED_MAIL_DOMAIN;

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
  public Response getLeads(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadsManagementService.getLeads()).build();
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
/*    String captureToken  = System.getProperty(LEAD_CAPTURE_TOKEN);
    if (headerToken == null) {
      LOG.warn("Security Token for Lead capture not defined");
      return Response.status(Response.Status.FORBIDDEN).entity("Access forbidden to the add lead rest service, Security Token not defined").build();
    }
    if (captureToken == null || !captureToken.equals(headerToken)) {
      LOG.warn("Access forbidden to the add lead rest service, wrong token: {}", headerToken);
      return Response.status(Response.Status.FORBIDDEN).entity("Access forbidden to the add lead rest service, wrong token").build();
    }*/
    if (lead == null) {
      LOG.warn("Lead not captured, lead is null");
      return Response.status(Response.Status.BAD_REQUEST).entity("Lead is null").build();
    }
    if (lead.getLead() == null || StringUtils.isEmpty(lead.getLead().getMail())) {
      LOG.warn("Lead not captured, mail needed");
      return Response.status(Response.Status.BAD_REQUEST).entity("Lead mail needed").build();
    }

    if (!leadCaptureSettingsService.getSettings().isCaptureEnabled()) {
      LOG.warn("Lead capture not enabled, New lead {} not captured ", lead.getLead().getId());
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    LOG.info("start adding lead {}", lead.toString());
    try {

      leadsManagementService.addLeadInfo(lead, true);
      LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_id:{},form_name:{}\"",
               lead.getLead().getId(),
              lead.getResponse()!=null?lead.getResponse().getFormName():"");

      return Response.status(Response.Status.OK).entity("lead synchronized")
                     .header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain())
                     .build();

    } catch (Exception e) {
      LOG.error("An error occured when trying to synchronise lead {}", lead.getLead(), e);
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

  @GET
  @Path("suspend/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response suspend(@Context UriInfo uriInfo, @HeaderParam("token") String headerToken, @PathParam("id") Long id) throws Exception {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
/*    String captureToken  = System.getProperty(LEAD_CAPTURE_TOKEN);
    if (headerToken == null) {
      LOG.warn("Security Token for Lead capture not defined");
      return Response.status(Response.Status.FORBIDDEN).entity("Access forbidden to the add lead rest service, Security Token not defined").build();
    }
    if (captureToken == null || !captureToken.equals(headerToken)) {
      LOG.warn("Access forbidden to the add lead rest service, wrong token: {}", headerToken);
      return Response.status(Response.Status.FORBIDDEN).entity("Access forbidden to the add lead rest service, wrong token").build();
    }*/
    try {
      leadsManagementService.suspendLead(id);
      LOG.info("Lead {} suspended", id);
      return Response.status(Response.Status.OK).entity("lead suspended")
              .header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain())
              .build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to suspend lead {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Access-Control-Allow-Origin", settings.getAllowedCaptureSourceDomain()).build();
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
      leadsManagementService.updateStatus(lead.getId(), lead.getStatus());
      LOG.info("Lead {} status updated to {} by {}", lead.getId(), lead.getStatus(), sourceIdentity.getRemoteId());
      return Response.status(Response.Status.OK).entity("lead status updated").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update lead {} status to {}", lead.getId(), lead.getStatus(), e);
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

}
