package org.exoplatform.leadcapture.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.leadcapture.dto.ResourceDTO;
import org.exoplatform.leadcapture.entity.ResourceEntity;
import org.exoplatform.leadcapture.services.ResourcesManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.Util;

import java.util.List;

@Path("/leadcapture/lcresources")
@Produces(MediaType.APPLICATION_JSON)

public class ResoucesManagementRest implements ResourceContainer {

  private final Log                  LOG                 = ExoLogger.getLogger(ResoucesManagementRest.class);

  private final String               portalContainerName = "portal";

  private ResourcesManagementService resourcesManagementService;

  public ResoucesManagementRest(ResourcesManagementService resourcesManagementService) {
    this.resourcesManagementService = resourcesManagementService;
  }

  @GET
  @Path("resources")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getResources(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(resourcesManagementService.getResources()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get resources list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("resources")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response add(@Context UriInfo uriInfo, ResourceDTO resourceDTO) throws Exception {
    try {
      resourcesManagementService.addResource(resourceDTO);
      return Response.ok("Resource added").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to add new resource {}", resourceDTO.getName(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DELETE
  @Path("resources/{id}")
  @RolesAllowed("ux-team")
  public Response delete(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      ResourceEntity resourceEntity = resourcesManagementService.getResourceById(id);
      if (resourceEntity == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Resource not found").build();
      }
      resourcesManagementService.deleteResource(resourceEntity);
      LOG.info("Resource {} deleted by {}", id, sourceIdentity.getRemoteId());
      return Response.ok("Resource deleted").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to delete Resource {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("resources")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response update(@Context UriInfo uriInfo, ResourceDTO resourceDTO) throws Exception {
    try {
      resourcesManagementService.updateResource(resourceDTO);
      return Response.ok("Resource updated").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update Resource {}", resourceDTO.getId(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }


  @POST
  @Path("resources/import")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("administrators")
  public Response importResources(@Context UriInfo uriInfo, List<ResourceDTO> resourceDTOs) throws Exception {
    try {
      for(ResourceDTO resourceDTO : resourceDTOs){
        resourceDTO.setId(null);
      resourcesManagementService.addResource(resourceDTO);
      }
      return Response.ok("Resources imported").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to import resources", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }


}
