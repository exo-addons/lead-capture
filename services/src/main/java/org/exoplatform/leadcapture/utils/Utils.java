package org.exoplatform.leadcapture.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.organization.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;

public class Utils {
  private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
  private static final Log LOG = ExoLogger.getLogger(Utils.class);
  private static final String MARKETING_GROUP_NAME_CONFIGURATION = "exo.addon.lc.marketing.group.name";



  public static JSONObject toResponseJson(ResponseEntity responseEntity) throws JSONException {
    JSONObject responseJson = new JSONObject();
    for (FieldEntity field : responseEntity.getFilelds()) {
      responseJson.put(field.getName(), field.getValue());
    }
    responseJson.put("createdDate", formatter.format(new Date(responseEntity.getCreatedDate())));
    return responseJson;
  }


  public static List<User> getMarketersList() {
    String groupId = PropertyManager.getProperty(MARKETING_GROUP_NAME_CONFIGURATION);
    if (groupId == null || groupId.isEmpty()){
      groupId = "marketing-team";
    }
    return getGroupMembers(groupId);
  }

  public static List<User> getGroupMembers(String groupId) {

    try {
      OrganizationService organizationService = CommonsUtils.getService(OrganizationService.class);
      ListAccess<User> engSupportList = organizationService.getUserHandler().findUsersByGroupId(groupId);
      User[] users = engSupportList.load(0, engSupportList.getSize());
      return Arrays.asList(users);
    }
    catch (Exception e) {
      LOG.warn("Cannot get the list of group members");
      return new ArrayList<User>();
    }
  }

  public static SimpleDateFormat getFormatter(){
    return formatter;
  }

}
