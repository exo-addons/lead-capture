package org.exoplatform.leadcapture.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class LeadDAO extends GenericDAOJPAImpl<LeadEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(LeadDAO.class);

  public LeadEntity getLeadByMail(String mail) {

    TypedQuery<LeadEntity> query = getEntityManager().createNamedQuery("LeadEntity.getLeadByMail", LeadEntity.class)
                                                     .setParameter("mail", mail);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get lead by email {}", mail, e);
      return null;
    }
  }

}
