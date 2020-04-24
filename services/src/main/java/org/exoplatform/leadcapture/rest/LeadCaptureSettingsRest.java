package org.exoplatform.leadcapture.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.Util;

@Path("/leadcapture/lcsettings")
@Produces(MediaType.APPLICATION_JSON)

public class LeadCaptureSettingsRest implements ResourceContainer {

  private final Log                  LOG                 = ExoLogger.getLogger(LeadCaptureSettingsRest.class);

  private final String               portalContainerName = "portal";

  private final String[]             SUPPORTED_FORMATS   = new String[] { "json" };

  private LeadCaptureSettingsService leadCaptureSettingsService;

  public LeadCaptureSettingsRest(LeadCaptureSettingsService leadCaptureSettingsService) {
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  @GET
  @Path("settings")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  public Response getSettings(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(leadCaptureSettingsService.getSettings()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get capture settings", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("settings")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  public Response save(@Context UriInfo uriInfo, LeadCaptureSettings settings) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      leadCaptureSettingsService.saveSettings(settings);
      LOG.info("Lead capture settings updated by {}", sourceIdentity.getRemoteId());
      return Response.status(Response.Status.NO_CONTENT).entity("Settings updated").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to set capture settings", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("context")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getContext(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
      JSONObject context = new JSONObject();
      context.put("currentUser", sourceIdentity.getRemoteId());
      context.put("currentUserFullName", sourceIdentity.getProfile().getFullName());
      context.put("isManager", isManager(sourceIdentity.getRemoteId(), settings.getUserExperienceGroup()));
      context.put("leadCaptureConfigured", isConfigured(settings));
      return Response.ok(context.toString()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get capture context", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  public boolean isManager(String userName, String group) {
    try {
      OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
      if (group != null
          && organizationService.getMembershipHandler().findMembershipByUserGroupAndType(userName, group, "manager") != null) {
        return true;
      }
    } catch (Exception e) {
      LOG.error("An error occured when trying check if user is a manager of the group {}", group, e);
      return false;
    }
    return false;
  }

  public boolean isConfigured(LeadCaptureSettings settings) {
    if (StringUtils.isNotEmpty(settings.getUserExperienceGroup()) && StringUtils.isNotEmpty(settings.getUserExperienceGroup())
        && StringUtils.isNotEmpty(settings.getUserExperienceSpace()) && StringUtils.isNotEmpty(settings.getSenderMail())) {
      return true;
    }
    return false;
  }
}
