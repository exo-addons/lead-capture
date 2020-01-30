package org.exoplatform.leadcapture;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.activity.model.ExoSocialActivityImpl;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.identity.provider.SpaceIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.service.ProjectService;
import org.exoplatform.task.util.ProjectUtil;
import org.exoplatform.ws.frameworks.json.JsonGenerator;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.*;

public class Utils {
  private static final Log             LOG                            = ExoLogger.getLogger(Utils.class);

  public static final String           LEAD_DEFAULT_STATUS            = "Raw";

  public static final String           LEAD_OPEN_STATUS            = "Open";

  public static final String           CREATION_DATE_FIELD_NAME       = "createdDate";

  public static final String           FIELDS_DELIMITER               = ",";

  public static final String           MAIL_DEFAULT_LANGUAGE          = "en";

  public static final String           NEW_LEAD_EVENT                 = "leadCapture.newLead.event";

  public static final String           NEW_RESPONSE_EVENT             = "leadCapture.newResponse.event";

  public static final String           DATE_FORMAT                    = "d MMM yyyy HH:mm:ss";

  public static final String           EMPTY_STR                      = "";

  public static final SimpleDateFormat formatter                      = new SimpleDateFormat(DATE_FORMAT);

  public static final String           LEAD_CAPTURE_SCOPE_NAME        = "ADDONS_LEAD_CAPTURE_SCOPE";

  public static final String           LEAD_CAPTURE_CONTEXT_NAME      = "ADDONS_LEAD_CAPTURE_CONTEXT";

  public static final Context          LEAD_CAPTURE_CONTEXT           = Context.GLOBAL.id(LEAD_CAPTURE_CONTEXT_NAME);

  public static final Scope            LEAD_CAPTURE_SCOPE             = Scope.APPLICATION.id(LEAD_CAPTURE_SCOPE_NAME);

  public static final String           LEAD_CAPTURE_SETTINGS_KEY_NAME = "LEAD_CAPTURE_SETTINGS";

  public static final String           USERS_EXPERENCE_GROUP_NAME     = "/platform/ux-team";

  public static final String           ALLOWED_MAIL_DOMAIN            = "leadCapture.allowed.mail.domain";

  public static final String           LEAD_CAPTURE_TOKEN             = "leadCapture.security.token";

  public static final String[]         LC_STATUSES                    = { "Open", "Attempted", "Contacted", "Qualified","Recycled", "Accepted"};

  public static final String           CASE_STUDY                     = "case-study";

  public static final String           WHITE_PAPER                    = "white-paper";

  public static final JsonParser       JSON_PARSER                    = new JsonParserImpl();

  public static final JsonGenerator    JSON_GENERATOR                 = new JsonGeneratorImpl();

  public static JSONObject toResponseJson(ResponseEntity responseEntity) {
    JSONObject responseJson = new JSONObject();
    try {
      for (FieldEntity field : responseEntity.getFilelds()) {
        responseJson.put(field.getName(), field.getValue());
      }
      responseJson.put(CREATION_DATE_FIELD_NAME, formatter.format(responseEntity.getCreatedDate()));
    } catch (JSONException e) {
      LOG.error("Cannot convert response {} to json", responseEntity.getId(), e);
    }
    return responseJson;
  }

  public static List<User> getGroupMembers(String groupId) {

    try {
      OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
      ListAccess<User> grpMembersList = organizationService.getUserHandler().findUsersByGroupId(groupId);
      User[] users = grpMembersList.load(0, grpMembersList.getSize());
      return Arrays.asList(users);
    } catch (Exception e) {
      LOG.error("Cannot get the list of group {} members", groupId, e);
      return new ArrayList<User>();
    }
  }

  public static MailContentDTO getContentForMail(MailTemplateDTO mailTemplateDTO, LeadEntity lead) {
    if (mailTemplateDTO.getContents().size() == 1) {
      return (mailTemplateDTO.getContents().get(0));
    } else {
      if (StringUtils.isEmpty(lead.getLanguage())) {
        lead.setLanguage(MAIL_DEFAULT_LANGUAGE);
      }
      for (MailContentDTO content_ : mailTemplateDTO.getContents()) {
        if (content_.getContent() != "" && content_.getSubject() != "" && lead.getLanguage().contains(content_.getLanguage())) {
          return (content_);
        }
      }
    }
    return (mailTemplateDTO.getContents().get(0));
  }

  public static ExoSocialActivity createActivity(LeadEntity lead) {
    LeadCaptureSettingsService leadCaptureSettingsService = CommonsUtils.getService(LeadCaptureSettingsService.class);
    String spaceName = leadCaptureSettingsService.getSettings().getUserExperienceSpace();
    String botName = leadCaptureSettingsService.getSettings().getUserExperienceBotUserName();
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    ActivityManager activityManager = CommonsUtils.getService(ActivityManager.class);
    Space space = spaceService.getSpaceByPrettyName(spaceName);
    if (space == null) {
      LOG.warn("Space not found");
      return null;
    }
    Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
    if (spaceIdentity == null) {
      LOG.warn("Not able to create the activity, the Space Identity is missing");
      return null;
    }
    Identity posterIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, botName);
    if (posterIdentity == null) {
      LOG.warn("Not able to create the activity, the Poster Identity is missing");
      return null;
    }
    ExoSocialActivity activity = new ExoSocialActivityImpl();
    activity.setType("DEFAULT_ACTIVITY");
    activity.setTitle("<span id='npsActivity'>\n" + "A new lead has been created: <br/>\n" + " <b>Lead mail : </b>"
        + lead.getMail() + "<br/>\n");
    activity.setUserId(posterIdentity.getId());
    return activityManager.saveActivity(spaceIdentity, activity);

  }

  public static Project getTaskProject(String groupId, String taskProject) {
    ProjectService projectService = CommonsUtils.getService(ProjectService.class);
    List<Project> projects = ProjectUtil.getProjectTree(groupId, projectService);
    if (taskProject != null) {
      for (Project project : projects) {
        if (project.getName().equals(taskProject)) {
          return project;
        }
      }
    }
    return projects.get(0);
  }

  public static JSONArray getCommentsJson(ListAccess<Comment> comments) {
    try {
      JSONArray commentsList = new JSONArray();
      OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
      for (Comment comment : comments.load(0, comments.getSize())) {
        commentsList.put(commentToJson(comment,
                                       comment.getAuthor(),
                                       organizationService.getUserHandler()
                                                          .findUserByName(comment.getAuthor())
                                                          .getDisplayName()));
      }
      return commentsList;
    } catch (Exception e) {
      LOG.error("Cannot convert the list of comments to json", e);
    }
    return null;
  }

  public static JSONObject commentToJson(Comment comment, String author, String authorName) {
    try {
      JSONObject commentJson = new JSONObject();
      commentJson.put("comment", comment.getComment());
      commentJson.put("author", author);
      commentJson.put("authorName", authorName);
      commentJson.put(CREATION_DATE_FIELD_NAME, formatter.format(comment.getCreatedTime()));
      return commentJson;
    } catch (Exception e) {
      LOG.error("Cannot convert comment {} to json", comment.getId(), e);
    }
    return null;
  }

  public static final String toJsonString(Object object) {
    try {
      return JSON_GENERATOR.createJsonObject(object).toString();
    } catch (JsonException e) {
      throw new IllegalStateException("Error parsing object to string " + object, e);
    }
  }

  public static final <T> T fromJsonString(String value, Class<T> resultClass) {
    try {
      if (StringUtils.isBlank(value)) {
        return null;
      }
      JsonDefaultHandler jsonDefaultHandler = new JsonDefaultHandler();
      JSON_PARSER.parse(new ByteArrayInputStream(value.getBytes()), jsonDefaultHandler);
      return ObjectBuilder.createObject(resultClass, jsonDefaultHandler.getJsonObject());
    } catch (JsonException e) {
      throw new IllegalStateException("Error creating object from string : " + value, e);
    }
  }

}
