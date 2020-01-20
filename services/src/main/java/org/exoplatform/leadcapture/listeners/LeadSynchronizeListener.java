package org.exoplatform.leadcapture.listeners;

import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class LeadSynchronizeListener extends Listener<FormInfo, String> {

  private static final Log       LOG = ExoLogger.getLogger(LeadSynchronizeListener.class);

  private LeadsManagementService leadsManagementService;

  public LeadSynchronizeListener(LeadsManagementService leadsManagementService) {
    this.leadsManagementService = leadsManagementService;
  }

  @Override
  public void onEvent(Event<FormInfo, String> event) throws Exception {
    FormInfo formInfo = event.getSource();
    leadsManagementService.synchronizeLead(formInfo);
  }
}
