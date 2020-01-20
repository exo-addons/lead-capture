package org.exoplatform.leadcapture.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.NotSynchroLeadEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class NotSynchronizedLeadsDAO extends GenericDAOJPAImpl<NotSynchroLeadEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(NotSynchronizedLeadsDAO.class);

  public NotSynchroLeadEntity getNotSynchLead(Long leadId, Long responseId) {

    TypedQuery<NotSynchroLeadEntity> query = getEntityManager()
                                                               .createNamedQuery("NotSynchroLeadEntity.getNotSynchLead",
                                                                                 NotSynchroLeadEntity.class)
                                                               .setParameter("leadId", leadId)
                                                               .setParameter("responseId", responseId);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get lead by leadId {} and responseId {}", leadId, responseId, e);
      return null;
    }
  }

}
