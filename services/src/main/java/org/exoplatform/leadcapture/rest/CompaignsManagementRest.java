package org.exoplatform.leadcapture.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.leadcapture.dto.CompaignDTO;
import org.exoplatform.leadcapture.entity.CompaignEntity;
import org.exoplatform.leadcapture.services.CompaignManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.Util;

@Path("/leadcapture/lccompaigns")
@Produces(MediaType.APPLICATION_JSON)

public class CompaignsManagementRest implements ResourceContainer {

  private final Log                  LOG                 = ExoLogger.getLogger(CompaignsManagementRest.class);

  private final String               portalContainerName = "portal";

  private CompaignManagementService compaignsManagementService;

  public CompaignsManagementRest(CompaignManagementService compaignsManagementService) {
    this.compaignsManagementService = compaignsManagementService;
  }

  @GET
  @Path("compaigns")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getCompaigns(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(compaignsManagementService.getCompaigns()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get compaigns list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("compaigns")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response add(@Context UriInfo uriInfo, CompaignDTO compaignDTO) throws Exception {
    try {
      compaignsManagementService.addCompaign(compaignDTO);
      return Response.ok("Compaign added").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to add new compaign {}", compaignDTO.getName(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DELETE
  @Path("compaigns/{id}")
  @RolesAllowed("ux-team")
  public Response delete(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      CompaignEntity compaignEntity = compaignsManagementService.getCompaignById(id);
      if (compaignEntity == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Compaign not found").build();
      }
      compaignsManagementService.deleteCompaign(compaignEntity);
      LOG.info("Compaign {} deleted by {}", id, sourceIdentity.getRemoteId());
      return Response.ok("Compaign deleted").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to delete Compaign {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("compaigns")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response update(@Context UriInfo uriInfo, CompaignDTO compaignDTO) throws Exception {
    try {
      compaignsManagementService.updateCompaign(compaignDTO);
      return Response.ok("Compaign updated").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update Compaign {}", compaignDTO.getId(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
