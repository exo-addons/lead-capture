package org.exoplatform.leadcapture.listeners;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dao.FieldDAO;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LeadCaptureSettingsService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.ActivityManager;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.legacy.service.TaskPayload;
import org.exoplatform.task.legacy.service.TaskService;

public class TaskUpdateListener extends Listener<TaskService, TaskPayload> {
  private static final Log  LOG = ExoLogger.getLogger(TaskUpdateListener.class);

  protected LeadsManagementService leadsManagementService;

  public TaskUpdateListener(LeadsManagementService leadsManagementService) {
    this.leadsManagementService = leadsManagementService;
  }

  @Override
  public void onEvent(Event<TaskService, TaskPayload> event) throws Exception {
    TaskPayload data = event.getData();
    Task before = data.before();
    Task after = data.after();
     LeadEntity leadEntity = leadsManagementService.getLeadByTask(before.getId());
     if(leadEntity!=null){
         if (before != null && after != null) {
             if (isDiff(before.getAssignee(), after.getAssignee())) {
                 leadEntity.setAssignee(after.getAssignee());
                 LOG.info("Lead {} associated to the Task {} has been assigned to {}",leadEntity.getId(), after.getId(), after.getAssignee());
             }
             if (isDiff(before.getStatus(), after.getStatus())) {
                 leadEntity.setStatus(after.getStatus().getName());
                 if(after.getStatus().getName().equals("Qualified")){
                     ActivityManager activityManager = CommonsUtils.getService(ActivityManager.class);
                     IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
                     String activityId = leadEntity.getActivityId();
                     ExoSocialActivity activity = activityManager.getActivity(activityId);
                     if (activity == null) {
                         throw new IllegalStateException("Activity with id '" + activityId + "' wasn't found");
                     }
                     String username = ConversationState.getCurrent().getIdentity().getUserId();
                     Identity posterIdentity = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username);
                     if (posterIdentity == null) {
                         LOG.warn("Not able to create the comment, the Poster Identity is missing");
                         throw new IllegalStateException("Not able to create the comment, the Poster Identity is missing");
                     }
                     String commentText = "Lead has beed Qualified";
                     Utils.saveComment(activity, commentText, posterIdentity.getId(), posterIdentity.getId());
                 }
                 LOG.info("Status of the lead {} associated to the Task {} has been cahanged to {}",leadEntity.getId(), after.getId(), after.getStatus().getName());
             }
         }
     }
  }

  private boolean isDiff(Object before, Object after) {
    if (before == after) {
      return false;
    }
    if (before != null) {
      return !before.equals(after);
    } else {
      return !after.equals(before);
    }
  }

}
