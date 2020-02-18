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

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;
import org.exoplatform.task.domain.Project;
import org.exoplatform.task.domain.Status;
import org.exoplatform.task.service.ProjectService;
import org.exoplatform.task.service.StatusService;

public class LeadCaptureSettingsService {

  private final Log           LOG = ExoLogger.getLogger(LeadCaptureSettingsService.class);

  private SettingService      settingService;

  private LeadCaptureSettings configuredLeadCaptureSettings;

  public LeadCaptureSettingsService(SettingService settingService) {
    this.settingService = settingService;
  }

  public LeadCaptureSettings getSettings() {
    if (this.configuredLeadCaptureSettings != null) {
      return this.configuredLeadCaptureSettings.clone();
    }
    SettingValue<?> settingsValue = settingService.get(LEAD_CAPTURE_CONTEXT, LEAD_CAPTURE_SCOPE, LEAD_CAPTURE_SETTINGS_KEY_NAME);
    String settingsValueString = settingsValue == null || settingsValue.getValue() == null ? null
                                                                                           : settingsValue.getValue().toString();
    org.exoplatform.leadcapture.dto.LeadCaptureSettings leadCaptureSettings = null;
    if (settingsValueString == null) {
      leadCaptureSettings = new org.exoplatform.leadcapture.dto.LeadCaptureSettings();
    } else {
      leadCaptureSettings = fromJsonString(settingsValueString, org.exoplatform.leadcapture.dto.LeadCaptureSettings.class);
    }
    if (leadCaptureSettings.getUserExperienceGroup() == null) {
      leadCaptureSettings.setUserExperienceGroup(USERS_EXPERENCE_GROUP_NAME);
    }
    this.configuredLeadCaptureSettings = leadCaptureSettings;
    return this.configuredLeadCaptureSettings;
  }

  public void saveSettings(LeadCaptureSettings leadCaptureSettings) {
    if (leadCaptureSettings == null) {
      throw new IllegalArgumentException("Empty settings to save");
    }

    LeadCaptureSettings oldSettings = getSettings();

    String settingsString = toJsonString(leadCaptureSettings);
    settingService.set(LEAD_CAPTURE_CONTEXT,
                       LEAD_CAPTURE_SCOPE,
                       LEAD_CAPTURE_SETTINGS_KEY_NAME,
                       SettingValue.create(settingsString));

    // Purge cached settings
    this.configuredLeadCaptureSettings = null;

    if (StringUtils.isEmpty(oldSettings.getUserExperienceSpace())||!oldSettings.getUserExperienceSpace().equals(leadCaptureSettings.getUserExperienceSpace())) {
      updateStatuses(leadCaptureSettings.getUserExperienceSpace(), leadCaptureSettings.getLeadTaskProject());
    } else if (StringUtils.isNotEmpty(leadCaptureSettings.getLeadTaskProject())
        && !oldSettings.getLeadTaskProject().equals(leadCaptureSettings.getLeadTaskProject())) {
      updateStatuses(leadCaptureSettings.getUserExperienceSpace(), leadCaptureSettings.getLeadTaskProject());
    }
  }

  void updateStatuses(String userExperienceSpace, String leadTaskProject) {
    SpaceService spaceService = CommonsUtils.getService(SpaceService.class);
    StatusService statusService = CommonsUtils.getService(StatusService.class);
    Space uxSpace = spaceService.getSpaceByPrettyName(userExperienceSpace);
    if (uxSpace != null) {
      Project project = Utils.getTaskProject(uxSpace.getGroupId(), leadTaskProject);
      if (project != null) {
        try {
          project = CommonsUtils.getService(ProjectService.class).getProject(project.getId());
          Status intStatus = statusService.createStatus(project, "intStatus");
          for (Status status : statusService.getStatuses(project.getId())) {
            if (!"intStatus".equals(status.getName()))
              statusService.removeStatus(status.getId());
          }
          for (String status_ : LC_STATUSES) {
            statusService.createStatus(project, status_);
          }
          statusService.removeStatus(intStatus.getId());
        } catch (Exception e) {
          LOG.error("Cannot update project statuses");
        }

      }

    }
  }

}
