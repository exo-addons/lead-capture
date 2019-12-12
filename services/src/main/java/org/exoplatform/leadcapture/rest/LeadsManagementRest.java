package org.exoplatform.leadcapture.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.dto.LeadDTO;
import org.exoplatform.leadcapture.services.LeadsManagement;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.RestChecker;
import org.exoplatform.social.service.rest.Util;

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
  @RolesAllowed("administrators")
  @Path("leads")
  public Response getLeads(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(leadsManagement.getLeads(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @RolesAllowed("administrators")
  @Path("leads")
  public Response add(@Context UriInfo uriInfo, FormInfo lead) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      leadsManagement.addLeadInfo(lead);
      return Response.ok("lead synchronized", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @RolesAllowed("administrators")
  @Path("responses")
  public Response getResponses(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(leadsManagement.getResponses(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
