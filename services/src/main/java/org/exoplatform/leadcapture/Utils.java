package org.exoplatform.leadcapture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
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
import static org.exoplatform.leadcapture.Constants.*;


public class Utils {
  private static final Log        LOG                                = ExoLogger.getLogger(Utils.class);
  private static SimpleDateFormat formatter                          = new SimpleDateFormat(DATE_FORMAT);

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
        if (content_.getContent()!="" && content_.getSubject()!="" && lead.getLanguage().contains(content_.getLanguage())) {
          return (content_);
        }
      }
    }
    return (mailTemplateDTO.getContents().get(0));
  }

  public static SimpleDateFormat getFormatter() {
    return formatter;
  }

}
