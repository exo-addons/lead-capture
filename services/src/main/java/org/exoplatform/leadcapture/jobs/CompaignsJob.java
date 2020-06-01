package org.exoplatform.leadcapture.jobs;

import static org.exoplatform.leadcapture.Utils.calculateNumberOfDays;

import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.commons.utils.ListAccess;
import org.exoplatform.leadcapture.Utils;
import org.exoplatform.leadcapture.dto.CompaignDTO;
import org.exoplatform.leadcapture.dto.MailContentDTO;
import org.exoplatform.leadcapture.dto.MailTemplateDTO;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.services.CompaignManagementService;
import org.exoplatform.leadcapture.services.LCMailService;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.task.domain.ChangeLog;
import org.exoplatform.task.domain.Task;
import org.exoplatform.task.service.TaskService;

public class CompaignsJob implements Job {
  private static final Log          LOG                       = ExoLogger.getLogger(CompaignsJob.class);

  private CompaignManagementService compaignManagementService = CommonsUtils.getService(CompaignManagementService.class);

  private LeadsManagementService    leadsManagementService    = CommonsUtils.getService(LeadsManagementService.class);

  private TaskService               taskService               = CommonsUtils.getService(TaskService.class);

  private LCMailService             lcMailService             = CommonsUtils.getService(LCMailService.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOG.info("Compaigns Job started");
    List<CompaignDTO> compaignDTOList = compaignManagementService.getCompaigns();
    for (CompaignDTO compaignDTO : compaignDTOList) {
      if (compaignDTO.getField().equals("status")) {
        List<LeadEntity> leadsEntities = leadsManagementService.getLeadsByStatus(compaignDTO.getValue());

        for (LeadEntity leadEntity : leadsEntities) {
          if (leadEntity != null) {
            if (leadEntity.getTaskId() != null && leadEntity.getTaskId() != 0) {
              try {
                Task task = taskService.getTask(leadEntity.getTaskId());
                ListAccess<ChangeLog> logs = taskService.getTaskLogs(leadEntity.getTaskId());
                for (ChangeLog log : logs.load(0, logs.getSize())) {
                  if (log.getActionName().equals("edit_status") && log.getTarget().equals(compaignDTO.getValue())) {
                    if (calculateNumberOfDays(new Date().getTime(), log.getCreatedTime()) + 1 == compaignDTO.getAfter()) {
                      MailContentDTO content = null;
                      MailTemplateDTO mailTemplateDTO = compaignDTO.getMailTemplateDTO();
                      if (mailTemplateDTO.getContents().size() > 0) {

                        content = Utils.getContentForMail(mailTemplateDTO, leadEntity);
                        if (content != null) {
                          lcMailService.sendMail(content.getContent(), content.getSubject(), leadEntity, null, null);
                          LOG.info("service=lead-capture operation=send_mail_to_lead parameters=\"lead_id:{},lead_name:{},mail_template_id:{},mail_template_name:{},reason: compaign\"",
                                   leadEntity.getId(),
                                   leadEntity.getFirstName() + " " + leadEntity.getLastName(),
                                   mailTemplateDTO.getId(),
                                   mailTemplateDTO.getName());
                        }
                      }
                    }
                  }
                }
              } catch (Exception e) {
                LOG.error("Cannot get Task log for lead {}", leadEntity.getId(), e);
              }
            }
          }
        }
      }
    }

    LOG.info("Compaigns Job started");
  }
}
