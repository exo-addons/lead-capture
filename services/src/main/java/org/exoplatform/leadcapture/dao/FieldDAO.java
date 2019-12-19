package org.exoplatform.leadcapture.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class FieldDAO extends GenericDAOJPAImpl<FieldEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(FieldDAO.class);

  public List<FieldEntity> getFileldsByResponse(long responseId) {

    TypedQuery<FieldEntity> query = getEntityManager().createNamedQuery("FieldEntity.getFileldsByResponse", FieldEntity.class)
                                                      .setParameter("responseId", responseId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of fields by formId {}", responseId, e);
      return null;
    }
  }
}
