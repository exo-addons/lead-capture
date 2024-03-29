package org.exoplatform.leadcapture.dao;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class FieldDAO extends GenericDAOJPAImpl<FieldEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(FieldDAO.class);

  public List<FieldEntity> getFieldsByResponse(long responseId) {

    TypedQuery<FieldEntity> query = getEntityManager().createNamedQuery("FieldEntity.getFieldsByResponse", FieldEntity.class)
                                                      .setParameter("responseId", responseId);
    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<FieldEntity>();
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of fields by formId {}", responseId, e);
      return new ArrayList<FieldEntity>();
    }
  }
}
