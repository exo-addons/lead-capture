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

import java.util.Date;

import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.services.listener.Asynchronous;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.organization.UserEventListener;

@Asynchronous
public class NewUserListener extends UserEventListener {

  private static final Log    LOG        = ExoLogger.getLogger(NewUserListener.class);
  private LeadDAO             leadDAO;

  public NewUserListener(LeadDAO leadDAO) throws Exception {
    this.leadDAO = leadDAO;
  }

  @Override
  public void postSave(User user, boolean isNew) throws Exception {

    try {
      LeadEntity leadEntity = leadDAO.getLeadByMail(user.getEmail());
      if (leadEntity != null) {
        if (leadEntity.getCommunityRegistration()==null || !leadEntity.getCommunityRegistration()) {
          leadEntity.setUpdatedDate(new Date().getTime());
          leadEntity.setCommunityRegistration(true);
          leadEntity.setCommunityRegistrationDate(user.getCreatedDate().getTime());
          leadEntity.setCommunityUserName(user.getUserName());
          leadDAO.update(leadEntity);
        }
      }
    } catch (Exception e) {
      LOG.error("an error accured", e);
    }
  }

  @Override
  public void postDelete(User user) throws Exception {
  }

}
