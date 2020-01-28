package org.exoplatform.leadcapture.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.MailTemplatesManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.Util;

@Path("/leadcapture/mailtemplatesmanagement")
@Produces(MediaType.APPLICATION_JSON)

public class MailTemplatesManagementRest implements ResourceContainer {

  private final Log                      LOG                 = ExoLogger.getLogger(MailTemplatesManagementRest.class);

  private final String                   portalContainerName = "portal";

  private MailTemplatesManagementService mailTemplatesManagementService;

  public MailTemplatesManagementRest(MailTemplatesManagementService mailTemplatesManagementService) {
    this.mailTemplatesManagementService = mailTemplatesManagementService;
  }

  @GET
  @Path("templates")
  @Produces(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response getTemplates(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      return Response.ok(mailTemplatesManagementService.getTemplates()).build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to get templates list", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("templates")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response add(@Context UriInfo uriInfo, MailTemplateDTO templateDTO) throws Exception {
    try {
      mailTemplatesManagementService.addTemplate(templateDTO);
      return Response.status(Response.Status.NO_CONTENT).entity("Mail template created").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to add new template {}", templateDTO.getName(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DELETE
  @Path("templates/{id}")
  @RolesAllowed("ux-team")
  public Response delete(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      MailTemplateEntity templateEntity = mailTemplatesManagementService.getTemplatebyId(id);
      if (templateEntity == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Lead Not found").build();
      }
      mailTemplatesManagementService.deleteTemplate(templateEntity);
      LOG.info("Template {} deleted by {}", id, sourceIdentity.getRemoteId());
      return Response.status(Response.Status.NO_CONTENT).entity("Mail template deleted").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to delete template {}", id, e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("templates")
  @Consumes(MediaType.APPLICATION_JSON)
  @RolesAllowed("ux-team")
  public Response update(@Context UriInfo uriInfo, MailTemplateDTO templateDTO) throws Exception {
    try {
      mailTemplatesManagementService.updateTemplate(templateDTO);
      return Response.status(Response.Status.NO_CONTENT).entity("Mail template updated").build();
    } catch (Exception e) {
      LOG.error("An error occured when trying to update template {}", templateDTO.getId(), e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
