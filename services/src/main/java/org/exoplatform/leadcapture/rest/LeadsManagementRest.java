package org.exoplatform.leadcapture.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.jaxrs.PATCH;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LeadsManagement;
import org.exoplatform.leadcapture.utils.Utils;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.RestChecker;
import org.exoplatform.social.service.rest.Util;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("/leadcapture/leadsmanagement")
@Produces(MediaType.APPLICATION_JSON)

public class LeadsManagementRest implements ResourceContainer {

  private final Log       LOG                 = ExoLogger.getLogger(LeadsManagementRest.class);

  private final String    portalContainerName = "portal";

  private final String[]  SUPPORTED_FORMATS   = new String[] { "json" };

  private LeadsManagement leadsManagement;

  public LeadsManagementRest(LeadsManagement leadsManagement) {
    this.leadsManagement = leadsManagement;
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
      return Response.ok(leadsManagement.getLeads(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("leads")
  public Response add(@Context UriInfo uriInfo, FormInfo lead) throws Exception {
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagement.addLeadInfo(lead);
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
      LeadEntity lead = leadsManagement.getLeadbyId(id);
      if (lead == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Lead Not found").build();
      }
      leadsManagement.deleteLead(lead);
      LOG.info("Webhook {} deleted by {}", id, sourceIdentity.getRemoteId());
      return Response.ok().build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("leads")
  public Response update(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagement.updateLead(lead);
      return Response.ok("lead updated", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PATCH
  @Path("assign")
  public Response assign(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagement.assigneLead(lead.getId(),lead.getAssignee());
      return Response.ok("lead assigned", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PATCH
  @Path("status")
  public Response updateStatus(@Context UriInfo uriInfo, LeadDTO lead) throws Exception {
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagement.updateStatus(lead.getId(),lead.getStatus());
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
      return Response.ok(leadsManagement.getResponses(id).toString(), mediaType).build();
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
      for(User user :Utils.getMarketersList()){
        JSONObject marketer = new JSONObject();
        marketer.put("userName",user.getUserName());
        marketer.put("fullName",user.getFirstName()+" "+user.getLastName());
        marketer.put("email",user.getEmail());
        marketersList.put(marketer);
      }
      return Response.ok(marketersList.toString(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }


}
