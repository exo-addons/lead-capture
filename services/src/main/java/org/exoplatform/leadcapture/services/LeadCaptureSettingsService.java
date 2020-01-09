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
import org.exoplatform.commons.api.settings.SettingService;
import org.exoplatform.commons.api.settings.SettingValue;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;

public class LeadCaptureSettingsService {

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
    String settingsValueString = settingsValue == null || settingsValue.getValue() == null ? null : settingsValue.getValue().toString();
    org.exoplatform.leadcapture.dto.LeadCaptureSettings leadCaptureSettings = null;
    if (settingsValueString == null) {
      leadCaptureSettings = new org.exoplatform.leadcapture.dto.LeadCaptureSettings();
    } else {
      leadCaptureSettings = fromJsonString(settingsValueString, org.exoplatform.leadcapture.dto.LeadCaptureSettings.class);
    }
    this.configuredLeadCaptureSettings = leadCaptureSettings;
    return this.configuredLeadCaptureSettings;
  }

  public void saveSettings(LeadCaptureSettings leadCaptureSettings) {
    if (leadCaptureSettings == null) {
      throw new IllegalArgumentException("Empty settings to save");
    }

    String settingsString = toJsonString(leadCaptureSettings);
    settingService.set(LEAD_CAPTURE_CONTEXT,
                       LEAD_CAPTURE_SCOPE,
                       LEAD_CAPTURE_SETTINGS_KEY_NAME,
                       SettingValue.create(settingsString));

    // Purge cached settings
    this.configuredLeadCaptureSettings = null;
  }

}
