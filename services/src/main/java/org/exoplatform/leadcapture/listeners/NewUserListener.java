/*
 * Copyright (C) 2003-2014 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.leadcapture.listeners;

import static org.exoplatform.leadcapture.Utils.*;

import java.util.Date;

import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.listener.ListenerService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

@Asynchronous
public class NewUserListener extends UserEventListener {

  private static final Log    LOG        = ExoLogger.getLogger(NewUserListener.class);
  private LeadDAO             leadDAO;

  private ListenerService listenerService;
  private LeadCaptureSettingsService leadCaptureSettingsService;

  public NewUserListener(LeadDAO leadDAO,LeadCaptureSettingsService leadCaptureSettingsService,
                         ListenerService listenerService) throws Exception {
    this.leadDAO = leadDAO;
    this.listenerService = listenerService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  @Override
  public void postSave(User user, boolean isNew) throws Exception {

    try {
      if (isNew && leadCaptureSettingsService.getSettings() != null && leadCaptureSettingsService.getSettings().isCaptureEnabled()) {
        LeadEntity lead = leadDAO.getLeadByMail(user.getEmail());
        if (lead != null) {
          if (lead.getCommunityRegistration() == null || !lead.getCommunityRegistration()) {
            lead.setUpdatedDate(new Date());
            lead.setCommunityRegistration(true);
            lead.setCommunityRegistrationDate(user.getCreatedDate());
            lead.setCommunityUserName(user.getUserName());
            leadDAO.update(lead);
            LOG.info("Lead {} has been associated to the community user {}", lead.getId(), user.getUserName());
          }
        } else {
          lead = new LeadEntity();
          lead.setMail(user.getEmail());
          lead.setFirstName(user.getFirstName());
          lead.setLastName(user.getLastName());
          lead.setCaptureMethod("Community registration");
          lead.setCaptureType("Register form");
          lead.setCaptureSourceInfo("Community registration");
          lead.setOriginalReferrer("");
          lead.setPersonSource("Web - Community");
          lead.setUpdatedDate(new Date());
          lead.setCreatedDate(new Date());
          lead.setCommunityRegistration(true);
          lead.setCommunityRegistrationDate(new Date());
          lead.setCommunityUserName(user.getUserName());
          lead.setCommunityRegistrationMethod("Register form");
          lead.setStatus(LEAD_DEFAULT_STATUS);
          lead.setPersonSource(getLeadSource(lead.getOriginalReferrer()));
          lead.setGeographiqueZone(getGeoZone(lead.getCountry()));
          leadDAO.create(lead);
          LOG.info("service=lead-capture operation=synchronize_lead parameters=\"lead_name:{},form_name:Community registration\"",
                  lead.getFirstName() + " " + lead.getLastName());
          listenerService.broadcast(NEW_LEAD_EVENT, lead, "");
        }
        LOG.info("Lead {} has been associated to the community user {}", lead.getId(), user.getUserName());
      }
      } catch(Exception e){
        LOG.error("an error occured", e);
      }

  }

  @Override
  public void postDelete(User user) throws Exception {
  }

}
