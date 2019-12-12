package org.exoplatform.leadcapture.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.List;

public class ResponseDAO extends GenericDAOJPAImpl<ResponseEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(ResponseDAO.class);


  public List<ResponseEntity> getResponsesByForm(long formId) {

    TypedQuery<ResponseEntity> query = getEntityManager()
            .createNamedQuery("ResponseEntity.getResponsesByForm",
                    ResponseEntity.class)
            .setParameter("formId", formId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of responses by formId {}", formId, e);
      return null;
    }
  }
}
