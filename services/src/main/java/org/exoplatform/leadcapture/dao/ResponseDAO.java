package org.exoplatform.leadcapture.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ResponseDAO extends GenericDAOJPAImpl<ResponseEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(ResponseDAO.class);

  public List<ResponseEntity> getResponsesByForm(long formId, long leadId) {

    TypedQuery<ResponseEntity> query = getEntityManager()
                                                         .createNamedQuery("ResponseEntity.getResponsesByFormAndLead",
                                                                           ResponseEntity.class)
                                                         .setParameter("formId", formId)
                                                         .setParameter("leadId", leadId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<ResponseEntity>();
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of responses by formId {}", formId, e);
      return new ArrayList<ResponseEntity>();
    }
  }

  public List<ResponseEntity> getResponsesByLead(long leadId) {

    TypedQuery<ResponseEntity> query = getEntityManager().createNamedQuery("ResponseEntity.getResponsesByLead",
                                                                           ResponseEntity.class)
                                                         .setParameter("leadId", leadId);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<ResponseEntity>();
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of responses by lead {}", leadId, e);
      return new ArrayList<ResponseEntity>();
    }
  }
}
