package org.exoplatform.leadcapture;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.exoplatform.commons.api.settings.data.Context;
import org.exoplatform.commons.api.settings.data.Scope;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.dto.LeadDTO;
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
import org.exoplatform.social.core.storage.api.ActivityStorage;
import org.exoplatform.task.domain.Comment;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.service.ProjectService;
import org.exoplatform.task.util.ProjectUtil;
import org.exoplatform.ws.frameworks.json.JsonGenerator;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.*;

public class Utils {
  private static final Log             LOG                                  = ExoLogger.getLogger(Utils.class);

  public static final String           LEAD_DEFAULT_STATUS                  = "Raw";

  public static final String           LEAD_OPEN_STATUS                     = "Open";

  public static final String[]         LEAD_BAD_STATUSES                    = { LEAD_DEFAULT_STATUS, "Bad Data", "Duplicate" };

  public static final String           CREATION_DATE_FIELD_NAME             = "createdDate";

  public static final String           FIELDS_DELIMITER                     = ",";

  public static final String           MAIL_DEFAULT_LANGUAGE                = "en";

  public static final String           NEW_LEAD_EVENT                       = "leadCapture.newLead.event";

  public static final String           NEW_RESPONSE_EVENT                   = "leadCapture.newResponse.event";

  public static final String           DATE_FORMAT                          = "d MMM yyyy HH:mm:ss";

  public static final String           TASK_DATE_FORMAT                     = "yyyy-MM-dd";

  public static final String           EMPTY_STR                            = "";

  public static final SimpleDateFormat formatter                            = new SimpleDateFormat(DATE_FORMAT);

  public static final SimpleDateFormat taskFormatter                        = new SimpleDateFormat(TASK_DATE_FORMAT);

  public static final String           LEAD_CAPTURE_SCOPE_NAME              = "ADDONS_LEAD_CAPTURE_SCOPE";

  public static final String           LEAD_CAPTURE_CONTEXT_NAME            = "ADDONS_LEAD_CAPTURE_CONTEXT";

  public static final Context          LEAD_CAPTURE_CONTEXT                 = Context.GLOBAL.id(LEAD_CAPTURE_CONTEXT_NAME);

  public static final Scope            LEAD_CAPTURE_SCOPE                   = Scope.APPLICATION.id(LEAD_CAPTURE_SCOPE_NAME);

  public static final String           LEAD_CAPTURE_SETTINGS_KEY_NAME       = "LEAD_CAPTURE_SETTINGS";

  public static final String           USERS_EXPERENCE_GROUP_NAME           = "/platform/ux-team";

  public static final String           ALLOWED_MAIL_DOMAIN                  = "leadCapture.allowed.mail.domain";

  public static final String           LEAD_CAPTURE_TOKEN                   = "leadCapture.security.token";

  public static final String           LC_SOURCE_SOCIAL_NAME                = "Direct";

  public static final String           LC_SOURCE_DIRECT_NAME                = "Social";

  public static final String           LC_SOURCE_ORGANIC_NAME               = "Search Organic";

  public static final String           LC_SOURCE_REFERRAL_NAME              = "Referral";

  public static final String[]         LC_STATUSES                          =
                                                   { "Open", "Attempted", "Contacted", "Qualified", "Recycled", "Accepted" };

  public static final String[]         LC_SOURCE_SOCIAL                     =
                                                        { "facebook", "twitter", "linkedin", "reddit", "quora", "youtube" };

  public static final String[]         LC_SOURCE_DIRECT                     = { "exoplatform" };

  public static final String[]         LC_SOURCE_ORGANIC                    =
                                                         { "google", "yahoo", "bing", "duckduckgo", "baidu", "qwant" };

  public static final String           LC_G_ZONE_US_CANADA_NAME             = "US-Canada";

  public static final String           LC_G_ZONE_WESTERN_EUROPE_NAME        = "Western Europe";

  public static final String           LC_G_ZONE_ESTERN_EUROPE_NAME         = "Eastern Europe";

  public static final String           LC_G_ZONE_LAT_AM_NAME                = "LatAm";

  public static final String           LC_G_ZONE_APAC_NAME                  = "APAC";

  public static final String           LC_G_ZONE_MEA_NAME                   = "MEA";

  public static final String[]         LC_G_ZONE_WESTERN_EUROPE             = { "Belgium", "Belgique", "BE", "Netherlands", "NL",
      "Luxembourg", "LU", "Denmark", "DK", "Finland", "FI", "Åland Islands", "Aland Islands", "Iceland", "IS", "Norway", "NO",
      "Sweden", "SE", "France", "French", "FR", "Germany", "DEIreland", "IE", "Italy", "IT", "Liechtenstein", "LI", "Monaco",
      "MC", "Portugal", "PT", "Spain", "SP", "Switzerland", "Suisse", "CH", "United Kingdom", "UK", "GB", "Guernsey",
      "Holy See (Vatican City State)", "Isle of Man", "Jersey", "Spain", "ES", "Greece", "GR", "Austria", "AT" };

  public static final String[]         LC_G_ZONE_ESTERN_EUROPE              = { "Albania", "AL", "Armenia", "Belarus", "BY",
      "Bosnia", "Bosnia and Herzegovina", "Bosnia & Herzegovina", "BA", "Bulgaria", "BG", "Croatia", "HR", "Cyprus",
      "Czech Republic", "CZ", "Estonia", "EE", "Hungary", "HU", "Lithuania", "LT", "Macedonia", "Malta", "MT", "Moldova", "MD",
      "Poland", "PL", "Romania", "RO", "Russia", "RU", "Slovakia", "SK", "Slovenia", "Ukraine", "UA", "Yugoslavia", "Andorra",
      "AD", "Gibraltar", "Greenland", "Bosnia and Herzegovina", "Georgia", "Azerbaijan", "Latvia", "Moldova, Republic of",
      "Serbia", "Azerbaijan", "Faroe Islands", "Montenegro", "San Marino", "Montenegro", "ME", "Georgia", "GE" };

  public static final String[]         LC_G_ZONE_APAC                       = { "Australia", "AU", "Bangladesh", "BD", "Brunei",
      "Cambodia", "KH", "China", "CN", "Comoros", "KM", "Guam", "GU", "Hong Kong", "HK", "India", "IN", "Indonesia", "ID",
      "Japan", "JP", "Korea", "Korea, Republic of", "KR", "Laos", "LA", "Macau", "MO", "Malaysia", "MY", "Maldives", "MV",
      "Myanmar", "MM", "Nepal", "NP", "New Zealand", "NZ", "Philippines", "PH", "Singapore", "SG", "Sri Lanka", "LK", "Taiwan",
      "TW", "Thailand", "TH", "Vietnam", "VN", "Asia/Pacific Region", "Brunei Darussalam", "French Polynesia",
      "Iran, Islamic Republic of", "Kazakstan", "Korea, Democratic People's Republic of", "Mongolia", "Russian Federation",
      "Fiji", "Kyrgyzstan", "Bhutan", "Lao People's Democratic Republic", "New Caledonia", "Palau", "Papua New Guinea",
      "Tajikistan", "Timor-Leste", "Vanuatu", "VU", "American Samoa", "AS" };

  public static final String[]         LC_G_ZONE_LAT_AM                     = { "Anguilla", "AI", "Argentina", "AR", "Bahamas",
      "BS", "Belize", "BZ", "Bolivia", "BO", "Brazil", "BR", "Cayman Islands", "KY", "Chile", "CL", "Colombia", "CO",
      "Costa Rica", "CR", "Cuba", "CU", "Dominica", "DM", "Dominican Republic", "DO", "Ecuador", "EC", "El Salvador", "SV",
      "Guatemala", "GT", "Guyana", "French Guiana", "GY", "Haiti", "HT", "Honduras", "HN", "Jamaica", "JM", "Mexico", "MX",
      "Nicaragua", "NI", "Panama", "PA", "Paraguay", "PY", "Peru", "PE", "Puerto Rico", "PR", "Suriname", "SR", "Uruguay", "UY",
      "Venezuela", "VE", "Barbados", "Martinique", "Trinidad and Tobago", "Virgin Islands, U.S.", "Grenada", "Guadeloupe",
      "ermuda", "Netherlands Antilles", "Saint Lucia" };

  public static final String[]         LC_G_ZONE_MEA                        = { "Afghanistan", "AF", "Algeria", "Algerie", "DZ",
      "Angola", "AO", "Bahrain", "BH", "Benin", "BJ", "Botswana", "BW", "Burkina Faso", "BF", "Burundi", "BI", "Cameroon", "CM",
      "Cape Verde", "CV", "Central African Republic", "CF", "Chad", "TD", "Congo", "CG", "Cote D'ivoire", "Ivory Coast", "CI",
      "Djibouti", "DJ", "Egypt", "EG", "Ethiopia", "ET", "Gabon", "GA", "Gambia", "GM", "Ghana", "GH", "Guinea", "French Guinea",
      "GN", "Iran", "IR", "Iraq", "IQ", "Israel", "IL", "Jordan", "JO", "Kazakhstan", "KZ", "Kenya", "KE", "Kuwait", "KW",
      "Lebanon", "LB", "Liberia", "LR", "Libya", "LY", "Malawi", "MW", "Mali", "ML", "Mauritania", "MR", "Mauritius", "MU",
      "Morocco", "Maroc", "MA", "Mozambique", "MZ", "Namibia", "NA", "Niger", "NE", "Nigeria", "NG", "Oman", "OM", "Pakistan",
      "PK", "Qatar", "QA", "Rwanda", "RW", "Saudi Arabia", "SA", "Senegal", "SN", "Somalia", "SO", "South Africa", "ZA", "Sudan",
      "SD", "Swaziland", "SZ", "Syrian Arab Republic", "Syria", "SY", "Tanzania", "TZ", "Togo", "TG", "Tunisia", "Tunisie", "TN",
      "Turkey", "TR", "Turkmenistan", "TM", "Uganda", "UG", "United Arab Emirates", "AE", "UAE", "Uzbekistan", "UZ",
      "Western Sahara", "EH", "Yemen", "YE", "Zambia", "ZM", "Zimbabwe", "ZW", "Congo, The Democratic Republic of the",
      "Libyan Arab Jamahiriya", "Madagascar", "Palestinian Territory", "Reunion", "Tanzania, United Republic of", "Guinea-Bissau",
      "Lesotho", "Mayotte", "Seychelles", "Sierra Leone" };

  public static final String[]         LC_G_ZONE_US_CANADA                  = { "Canada", "CA", "United States", "US", "USA",
      "Virgin Islands", "VI", "Aruba", "AW", "Curacao" };

  public static final String[]         LC_CAPTURE_METHODE_CONTACT_US        = { "contactFormEn", "contactFormFr" };

  public static final String[]         LC_CAPTURE_METHODE_DEMO_REQUEST      = { "demoFormEn", "demoFormFr" };

  public static final String[]         LC_CAPTURE_METHODE_RESOURCE_DOWNLOAD = { "whitePaperFormEn", "whitePaperFormFr",
      "caseFormEn", "caseFormFr" };

  public static final String[]         LC_CAPTURE_METHODE_REWARD            = { "RewardFormEn", "RewardFormFr" };

  public static final String           LC_CONTACT_US                        = "contact-us";

  public static final String           LC_DEMO_REQUEST                      = "demo-request";

  public static final String           LC_RESOURCE_DOWNLOAD                 = "resource-download";

  public static final String           LC_REWARD                            = "reward-form";

  public static final String           CASE_STUDY                           = "case-study";

  public static final String           WHITE_PAPER                          = "white-paper";

  public static final JsonParser       JSON_PARSER                          = new JsonParserImpl();

  public static final JsonGenerator    JSON_GENERATOR                       = new JsonGeneratorImpl();

  public static JSONObject toResponseJson(ResponseEntity responseEntity) {
    JSONObject responseJson = new JSONObject();
    try {
      for (FieldEntity field : responseEntity.getFilelds()) {
        responseJson.put(field.getName(), field.getValue());
      }
      responseJson.put("id", responseEntity.getId());
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
    ActivityStorage activityStorage = CommonsUtils.getService(ActivityStorage.class);
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
    String userName = "<a class=\"textBold linkTitle\"  href=\""
        + leadCaptureSettingsService.getSettings().getLeadManagementAppUrl() + "?leadid=" + lead.getId() + "\">"
        + lead.getFirstName() + " " + lead.getLastName() + " </a>";
    userName = StringEscapeUtils.unescapeHtml(userName);
    // activity.setType("DEFAULT_ACTIVITY");
    String title = "<span id='lcActivity'>\n" + "A new lead has been created: <br/>\n" + " <b>Name : </b>" + userName + "<br/>\n"
        + " <b>mail : </b>" + lead.getMail() + "<br/>\n" + " <b>Country : </b>" + lead.getCountry() + "<br/>\n"
        + " <b>Company : </b>" + lead.getCompany() + "<br/>\n" + " <b>Capture methode : </b>" + lead.getCaptureMethod()
        + "<br/>\n";
    activity.setTitle(StringEscapeUtils.unescapeHtml(title));
    activity.setUserId(posterIdentity.getId());
    return activityStorage.saveActivity(spaceIdentity, activity);

  }

  public static void saveComment(String activityId, ResponseEntity responseEntity) {
    LeadCaptureSettingsService leadCaptureSettingsService = CommonsUtils.getService(LeadCaptureSettingsService.class);
    ActivityManager activityManager = CommonsUtils.getService(ActivityManager.class);
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    String botName = settings.getUserExperienceBotUserName();
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    ActivityStorage activityStorage = CommonsUtils.getService(ActivityStorage.class);
    FieldDAO fieldDAO = CommonsUtils.getService(FieldDAO.class);
    ExoSocialActivity activity = activityManager.getActivity(activityId);
    if (activity == null) {
      throw new IllegalStateException("Activity with id '" + activityId + "' wasn't found");
    }
    Identity posterIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, botName);
    if (posterIdentity == null) {
      LOG.warn("Not able to create the comment, the Poster Identity is missing");
      throw new IllegalStateException("Not able to create the comment, the Poster Identity is missing");
    }
    String commentText = "<span>\n" + "A new response has been added: <br/>" + "Form Name :"
        + responseEntity.getFormEntity().getName() + "<br/>\n";
    for (FieldEntity fieldEntity : fieldDAO.getFieldsByResponse(responseEntity.getId())) {
      if (!fieldEntity.getName().equals(CREATION_DATE_FIELD_NAME)) {
        commentText = commentText.concat(fieldEntity.getName() + " : " + fieldEntity.getValue() + "<br/>");
      }
    }
    ExoSocialActivity comment = new ExoSocialActivityImpl();
    comment.setTitle(commentText);
    comment.setUserId(posterIdentity.getId());
    comment.setPosterId(posterIdentity.getId());
    activityStorage.saveComment(activity, comment);

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

  public static boolean isResourceRequest(String field) {
    LeadCaptureSettingsService leadCaptureSettingsService = CommonsUtils.getService(LeadCaptureSettingsService.class);
    String identifiers = leadCaptureSettingsService.getSettings().getResourcesIdentifier();
    if (org.apache.commons.lang.StringUtils.isNotEmpty(identifiers)) {
      for (String identifier : identifiers.split(FIELDS_DELIMITER)) {
        if (field.contains(identifier)) {
          return true;
        }
      }
    }

    return false;
  }

  public static String getLeadSource(String originalReferrer) {
    if (StringUtils.isNoneEmpty(originalReferrer)) {
      if (isInList(originalReferrer, LC_SOURCE_DIRECT))
        return LC_SOURCE_DIRECT_NAME;
      if (isInList(originalReferrer, LC_SOURCE_SOCIAL))
        return LC_SOURCE_SOCIAL_NAME;
      if (isInList(originalReferrer, LC_SOURCE_ORGANIC))
        return LC_SOURCE_ORGANIC_NAME;
    }
    return LC_SOURCE_REFERRAL_NAME;
  }

  public static String getGeoZone(String country) {
    if (StringUtils.isNoneEmpty(country)) {
      if (isInList(country, LC_G_ZONE_US_CANADA))
        return LC_G_ZONE_US_CANADA_NAME;
      if (isInList(country, LC_G_ZONE_WESTERN_EUROPE))
        return LC_G_ZONE_WESTERN_EUROPE_NAME;
      if (isInList(country, LC_G_ZONE_ESTERN_EUROPE))
        return LC_G_ZONE_ESTERN_EUROPE_NAME;
      if (isInList(country, LC_G_ZONE_LAT_AM))
        return LC_G_ZONE_LAT_AM_NAME;
      if (isInList(country, LC_G_ZONE_APAC))
        return LC_G_ZONE_APAC_NAME;
      if (isInList(country, LC_G_ZONE_MEA))
        return LC_G_ZONE_MEA_NAME;
    }
    return "Zone not defined";
  }

  public static String getCaptureMethode(String form) {
    if (StringUtils.isNoneEmpty(form)) {
      if (isInList(form, LC_CAPTURE_METHODE_CONTACT_US))
        return LC_CONTACT_US;
      if (isInList(form, LC_CAPTURE_METHODE_DEMO_REQUEST))
        return LC_DEMO_REQUEST;
      if (isInList(form, LC_CAPTURE_METHODE_RESOURCE_DOWNLOAD))
        return LC_RESOURCE_DOWNLOAD;
      if (isInList(form, LC_CAPTURE_METHODE_REWARD))
        return LC_REWARD;
    }
    return form;
  }

  public static boolean isInList(String referrer, String[] sources) {
    for (String source : sources) {
      if (referrer.contains(source)) {
        return true;
      }
    }
    return false;
  }

}
