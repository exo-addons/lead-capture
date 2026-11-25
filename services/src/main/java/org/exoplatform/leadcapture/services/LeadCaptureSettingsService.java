/*
 * Copyright (C) 2003-2019 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.exoplatform.leadcapture.services;

import static org.exoplatform.leadcapture.Utils.*;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.task.dto.ProjectDto;
import org.exoplatform.task.dto.StatusDto;
import org.exoplatform.task.service.ProjectService;
import org.exoplatform.task.service.StatusService;

public class LeadCaptureSettingsService {

  private static final String MANAGER_MEMBERSHIP_TYPE = "manager";

  private final Log           LOG                     = ExoLogger.getLogger(LeadCaptureSettingsService.class);

  private SettingService      settingService;

  private OrganizationService organizationService;

  public LeadCaptureSettingsService(SettingService settingService, OrganizationService organizationService) {
    this.settingService = settingService;
    this.organizationService = organizationService;
  }

  /**
   * Reads the settings from the {@link SettingService} on every call.
   * <p>
   * There is deliberately no local copy: the platform {@link SettingService}
   * swallows any exception raised while reading a value and returns
   * {@code null} instead, exactly as if nothing had been stored. A local cache
   * populated from such a transient failure would serve the default (empty)
   * settings until the next restart or save, which is how production settings
   * used to "disappear". The {@link SettingService} already caches values in a
   * cluster-aware cache, so reading through it costs nothing.
   *
   * @return the stored settings, or default settings when nothing is stored
   *         (or when the store could not be read)
   */
  public LeadCaptureSettings getSettings() {
    SettingValue<?> settingsValue = settingService.get(LEAD_CAPTURE_CONTEXT, LEAD_CAPTURE_SCOPE, LEAD_CAPTURE_SETTINGS_KEY_NAME);
    String settingsValueString = settingsValue == null || settingsValue.getValue() == null ? null
                                                                                           : settingsValue.getValue().toString();
    LeadCaptureSettings leadCaptureSettings = StringUtils.isBlank(settingsValueString) ? null
                                                                                       : fromJsonString(settingsValueString,
                                                                                                        LeadCaptureSettings.class);
    if (leadCaptureSettings == null) {
      LOG.debug("No lead capture settings found in the settings store, using default settings");
      leadCaptureSettings = new LeadCaptureSettings();
    }
    if (leadCaptureSettings.getUserExperienceGroup() == null) {
      leadCaptureSettings.setUserExperienceGroup(USERS_EXPERENCE_GROUP_NAME);
    }
    return leadCaptureSettings;
  }

  public void saveSettings(LeadCaptureSettings leadCaptureSettings) throws Exception {
    if (leadCaptureSettings == null) {
      throw new IllegalArgumentException("Empty settings to save");
    }

    LeadCaptureSettings oldSettings = getSettings();

    String settingsString = toJsonString(leadCaptureSettings);
    settingService.set(LEAD_CAPTURE_CONTEXT,
                       LEAD_CAPTURE_SCOPE,
                       LEAD_CAPTURE_SETTINGS_KEY_NAME,
                       SettingValue.create(settingsString));

    try {
      if (StringUtils.isEmpty(oldSettings.getUserExperienceSpace())
          || !oldSettings.getUserExperienceSpace().equals(leadCaptureSettings.getUserExperienceSpace())) {
        updateStatuses(leadCaptureSettings.getUserExperienceSpace(), leadCaptureSettings.getLeadTaskProject());
      } else if (StringUtils.isNotEmpty(leadCaptureSettings.getLeadTaskProject())
          && !oldSettings.getLeadTaskProject().equals(leadCaptureSettings.getLeadTaskProject())) {
        updateStatuses(leadCaptureSettings.getUserExperienceSpace(), leadCaptureSettings.getLeadTaskProject());
      }
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  /**
   * @param userName the user to check
   * @return {@code true} when the user holds the {@code manager} membership in
   *         the configured UX team group (see
   *         {@link LeadCaptureSettings#getUserExperienceGroup()}), {@code false}
   *         otherwise or when the membership cannot be looked up
   */
  public boolean isUxTeamManager(String userName) {
    if (StringUtils.isBlank(userName)) {
      return false;
    }
    String uxTeamGroup = getSettings().getUserExperienceGroup();
    if (StringUtils.isBlank(uxTeamGroup)) {
      return false;
    }
    try {
      return organizationService.getMembershipHandler()
                                .findMembershipByUserGroupAndType(userName, uxTeamGroup, MANAGER_MEMBERSHIP_TYPE) != null;
    } catch (Exception e) {
      LOG.warn("Cannot check whether user {} is a manager of group {}", userName, uxTeamGroup, e);
      return false;
    }
  }

  void updateStatuses(String userExperienceSpace, String leadTaskProject) throws Exception {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    StatusService statusService = CommonsUtils.getService(StatusService.class);
    Space uxSpace = spaceService.getSpaceByPrettyName(userExperienceSpace);
    if (uxSpace != null) {
      ProjectDto project = Utils.getTaskProject(uxSpace.getGroupId(), leadTaskProject);
      if (project != null) {
        try {
          project = CommonsUtils.getService(ProjectService.class).getProject(project.getId());
          StatusDto intStatus = statusService.createStatus(project, "intStatus");
          for (StatusDto status : statusService.getStatuses(project.getId())) {
            if (!"intStatus".equals(status.getName()))
              statusService.removeStatus(status.getId());
          }
          for (String status_ : LC_STATUSES) {
            statusService.createStatus(project, status_);
          }
          statusService.removeStatus(intStatus.getId());
        } catch (Exception e) {
          LOG.error("Cannot update project statuses");
          throw e;
        }

      }

    }
  }

}
