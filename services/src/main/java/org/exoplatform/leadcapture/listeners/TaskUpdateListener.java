package org.exoplatform.leadcapture.listeners;

import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.listener.Event;
import org.exoplatform.services.listener.Listener;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.service.TaskPayload;
import org.exoplatform.task.service.TaskService;

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
             }
             if (isDiff(before.getStatus(), after.getStatus())) {
                 leadEntity.setStatus(after.getStatus().getName());
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
