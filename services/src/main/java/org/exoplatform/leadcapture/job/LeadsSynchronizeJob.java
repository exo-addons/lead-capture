package org.exoplatform.leadcapture.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.leadcapture.dao.NotSynchronizedLeadsDAO;
import org.exoplatform.leadcapture.dto.FormInfo;
import org.exoplatform.leadcapture.entity.NotSynchroLeadEntity;
import org.exoplatform.leadcapture.services.LeadsManagementService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class LeadsSynchronizeJob implements Job {

  private final Log LOG = ExoLogger.getLogger(LeadsSynchronizeJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LeadsManagementService leadsManagementService = CommonsUtils.getService(LeadsManagementService.class);
    NotSynchronizedLeadsDAO notSynchronizedLeadsDAO = CommonsUtils.getService(NotSynchronizedLeadsDAO.class);
    List<NotSynchroLeadEntity> notSynchroLeadEntityList = notSynchronizedLeadsDAO.findAll();
    for (NotSynchroLeadEntity notSynchroLeadEntity : notSynchroLeadEntityList) {
      try {
        FormInfo formInfo = new FormInfo(notSynchroLeadEntity.getId(),
                                         leadsManagementService.toLeadDto(notSynchroLeadEntity.getLeadEntity()),
                                         leadsManagementService.toResponseDto(notSynchroLeadEntity.getResponseEntity()));
        leadsManagementService.synchronizeLead(formInfo);
      } catch (Exception e) {
        LOG.error("Cannot synchronise lead", e);
      }
    }
  }
}
