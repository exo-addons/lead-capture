package org.exoplatform.leadcapture;

import static org.exoplatform.leadcapture.Constants.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
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

public class Utils {
  private static final Log        LOG       = ExoLogger.getLogger(Utils.class);

  private static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

  public static JSONObject toResponseJson(ResponseEntity responseEntity) throws JSONException {
    JSONObject responseJson = new JSONObject();
    for (FieldEntity field : responseEntity.getFilelds()) {
      responseJson.put(field.getName(), field.getValue());
    }
    responseJson.put(CREATION_DATE_FIELD_NAME, formatter.format(new Date(responseEntity.getCreatedDate())));
    return responseJson;
  }

  public static List<User> getMarketersList() {
    String groupId = PropertyManager.getProperty(MARKETING_GROUP_NAME_CONFIGURATION);
    if (groupId == null || groupId.isEmpty()) {
      groupId = DEFAULT_MARKETING_GROUP_NAME;
    }
    return getGroupMembers(groupId);
  }

  public static List<User> getGroupMembers(String groupId) {

    try {
      OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
      ListAccess<User> engSupportList = organizationService.getUserHandler().findUsersByGroupId(groupId);
      User[] users = engSupportList.load(0, engSupportList.getSize());
      return Arrays.asList(users);
    } catch (Exception e) {
      LOG.warn("Cannot get the list of group members");
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

  public static SimpleDateFormat getFormatter() {
    return formatter;
  }

  public static ExoSocialActivity createActivity(LeadEntity lead) {
    String spaceName = PropertyManager.getProperty(MARKETING_SPACE_NAME_CONFIGURATION);
    if (spaceName == null || spaceName.isEmpty()) {
      spaceName = DEFAULT_MARKETING_SPACE_NAME;
    }
    String botName = PropertyManager.getProperty(LEAD_CAPTURE_BOT_NAME_CONFIGURATION);
    if (botName == null || botName.isEmpty()) {
      botName = DEFAULT_LEAD_CAPTURE_BOT_NAME;
    }
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    ActivityManager activityManager = CommonsUtils.getService(ActivityManager.class);
    Space space = spaceService.getSpaceByPrettyName(spaceName);
    if (space == null) {
      LOG.warn("Space not found");
    } else {
      Identity spaceIdentity = identityManager.getOrCreateIdentity(SpaceIdentityProvider.NAME, space.getPrettyName());
      Identity posterIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, botName);

      if (posterIdentity != null && spaceIdentity != null) {
        ExoSocialActivity activity = new ExoSocialActivityImpl();
        activity.setType("DEFAULT_ACTIVITY");
        activity.setTitle("<span id='npsActivity'>\n" + "A new lead has been created: <br/>\n" + " <b>Lead mail : </b>"
            + lead.getMail() + "<br/>\n");
        activity.setUserId(posterIdentity.getId());
        return activityManager.saveActivity(spaceIdentity, activity);
      } else {
        LOG.warn("Not able to create the activity, the Poster or Space Identity is missing");
      }
    }

    return null;
  }

  public static Space getMarketingSpace() {
    String spaceName = PropertyManager.getProperty(MARKETING_SPACE_NAME_CONFIGURATION);
    if (spaceName == null || spaceName.isEmpty()) {
      spaceName = DEFAULT_MARKETING_SPACE_NAME;
      SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
      return (spaceService.getSpaceByPrettyName(spaceName));
    }
    return null;
  }

  public static Project getTaskProject() {
    ProjectService projectService = CommonsUtils.getService(ProjectService.class);
    Space marketingSpace = Utils.getMarketingSpace();
    List<Project> projects = ProjectUtil.getProjectTree(marketingSpace.getGroupId(), projectService);
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
      LOG.error("Cannot get list of comments for the Task", e);
    }
    return null;
  }

  public static JSONObject commentToJson(Comment comment, String author, String authorName) {
    try {
      JSONObject commentJson = new JSONObject();
      commentJson.put("comment", comment.getComment());
      commentJson.put("author", author);
      commentJson.put("authorName", authorName);
      commentJson.put(CREATION_DATE_FIELD_NAME, Utils.getFormatter().format(comment.getCreatedTime()));
      return commentJson;
    } catch (Exception e) {
      LOG.error("Cannot conevert comment to json", e);
    }
    return null;
  }

}
