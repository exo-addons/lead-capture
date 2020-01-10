package org.exoplatform.leadcapture.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.RestChecker;
import org.exoplatform.social.service.rest.Util;

import io.swagger.jaxrs.PATCH;

@Path("/leadcapture/leadsmanagement")
@Produces(MediaType.APPLICATION_JSON)

public class LeadsManagementRest implements ResourceContainer {

  private final Log                  LOG                 = ExoLogger.getLogger(LeadsManagementRest.class);

  private final String               portalContainerName = "portal";

  private final String[]             SUPPORTED_FORMATS   = new String[] { "json" };

  private LeadsManagementService     leadsManagementService;

  private LeadCaptureSettingsService leadCaptureSettingsService;

  public LeadsManagementRest(LeadsManagementService leadsManagementService,
                             LeadCaptureSettingsService leadCaptureSettingsService) {
    this.leadsManagementService = leadsManagementService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  @GET
  @Path("leads")
  public Response getLeads(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(leadsManagementService.getLeads(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("leads")
  public Response add(@Context UriInfo uriInfo, FormInfo lead) throws Exception {
    if (!leadCaptureSettingsService.getSettings().isCaptureEnabled()) {
      LOG.warn("Lead capture not enabled, New lead {} not captured ",lead.getLead().getMail());
      return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
    }
      MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
      try {
        leadsManagementService.addLeadInfo(lead, true);
        LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_mail:{},form_name:{}\"", lead.getLead().getMail(), lead.getResponse().getFormName());
        return Response.ok("lead synchronized", mediaType).build();
      } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
      }
  }

  @DELETE
  @Path("leads/{id}")
  public Response deletelead(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
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
      LOG.info("Lead {} deleted by {}", lead.getMail(), sourceIdentity.getRemoteId());
      return Response.ok().build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("leads/{id}")
  public Response update(@Context UriInfo uriInfo, @PathParam("id") Long id, LeadDTO lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagementService.updateLead(lead);
      LOG.info("Lead {} edited by {}",lead.getMail(), sourceIdentity.getRemoteId());
      return Response.ok("lead updated", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PATCH
  @Path("assign")
  public Response assign(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagementService.assigneLead(lead.getId(), lead.getAssignee());
      LOG.info("Lead {} assigned to {} by {}",lead.getMail(), lead.getAssignee(), sourceIdentity.getRemoteId());
      return Response.ok("lead assigned", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PATCH
  @Path("status")
  public Response updateStatus(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagementService.updateStatus(lead.getId(), lead.getStatus());
      LOG.info("Lead {} status updated to {} by {}",lead.getMail(), lead.getStatus(), sourceIdentity.getRemoteId());
      return Response.ok("lead status updated", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("responses/{id}")
  public Response getResponses(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(leadsManagementService.getResponses(id).toString(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("comments/{taskid}")
  public Response addComment(@Context UriInfo uriInfo, @PathParam("taskid") long taskId, String comment) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(leadsManagementService.addTaskComment(taskId, sourceIdentity.getRemoteId(), comment).toString(),
                         mediaType)
                     .build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("comments/{taskid}")
  public Response getComments(@Context UriInfo uriInfo, @PathParam("taskid") long taskId) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(leadsManagementService.getTaskComments(taskId).toString(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("marketers")
  public Response getMarketers(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      JSONArray marketersList = new JSONArray();
      for (User user : Utils.getGroupMembers(leadCaptureSettingsService.getSettings().getMarketingGroup())) {
        JSONObject marketer = new JSONObject();
        marketer.put("userName", user.getUserName());
        marketer.put("fullName", user.getFirstName() + " " + user.getLastName());
        marketer.put("email", user.getEmail());
        marketersList.put(marketer);
      }
      return Response.ok(marketersList.toString(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
