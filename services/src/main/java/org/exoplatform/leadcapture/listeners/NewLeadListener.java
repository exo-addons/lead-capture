package org.exoplatform.leadcapture.listeners;

import java.util.Map;

import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.MailTemplatesManagement;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class NewLeadListener extends Listener<LeadEntity, String> {

  private static final Log LOG = ExoLogger.getLogger(NewLeadListener.class);

  private LCMailService lcMailService;
  private MailTemplatesManagement mailTemplatesManagement;

  public NewLeadListener(LCMailService lcMailService, MailTemplatesManagement mailTemplatesManagement) {
    this.lcMailService = lcMailService;
    this.mailTemplatesManagement = mailTemplatesManagement;
  }

  @Override
  public void onEvent(Event<LeadEntity, String> event) throws Exception {
//todo
  }
}
