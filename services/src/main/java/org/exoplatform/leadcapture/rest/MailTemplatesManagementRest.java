package org.exoplatform.leadcapture.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.services.MailTemplatesManagement;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.service.rest.RestChecker;
import org.exoplatform.social.service.rest.Util;

@Path("/leadcapture/mailtemplatesmanagement")
@Produces(MediaType.APPLICATION_JSON)

public class MailTemplatesManagementRest implements ResourceContainer {

  private final Log               LOG                 = ExoLogger.getLogger(MailTemplatesManagementRest.class);

  private final String            portalContainerName = "portal";

  private final String[]          SUPPORTED_FORMATS   = new String[] { "json" };

  private MailTemplatesManagement mailTemplatesManagement;

  public MailTemplatesManagementRest(MailTemplatesManagement mailTemplatesManagement) {
    this.mailTemplatesManagement = mailTemplatesManagement;
  }

  @GET
  @Path("template")
  public Response getTemplates(@Context UriInfo uriInfo) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      return Response.ok(mailTemplatesManagement.getTemplates(), mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @POST
  @Path("template")
  public Response add(@Context UriInfo uriInfo, MailTemplateDTO templateDTO) throws Exception {
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      mailTemplatesManagement.addTemplate(templateDTO);
      return Response.ok("lead synchronized", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DELETE
  @Path("template/{id}")
  public Response delete(@Context UriInfo uriInfo, @PathParam("id") Long id) throws Exception {
    Identity sourceIdentity = Util.getAuthenticatedUserIdentity(portalContainerName);
    if (sourceIdentity == null) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    try {
      MailTemplateEntity templateEntity = mailTemplatesManagement.getTemplatebyId(id);
      if (templateEntity == null) {
        return Response.status(Response.Status.NOT_FOUND).entity("Lead Not found").build();
      }
      mailTemplatesManagement.deleteTemplate(templateEntity);
      LOG.info("Template {} deleted by {}", id, sourceIdentity.getRemoteId());
      return Response.ok().build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
    }
  }

  @PUT
  @Path("template")
  public Response update(@Context UriInfo uriInfo, MailTemplateDTO templateDTO) throws Exception {
    MediaType mediaType = RestChecker.checkSupportedFormat("json", SUPPORTED_FORMATS);
    try {
      mailTemplatesManagement.updateTemplate(templateDTO);
      return Response.ok("template updated", mediaType).build();
    } catch (Exception e) {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

}
