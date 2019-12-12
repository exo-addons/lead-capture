package org.exoplatform.leadcapture.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class FormDAO extends GenericDAOJPAImpl<FormEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(FormDAO.class);


  public FormEntity getFormByName(String name) {

    TypedQuery<FormEntity> query = getEntityManager()
            .createNamedQuery("FormEntity.getFormByName",FormEntity.class)
            .setParameter("name", name);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get form by name {}", name, e);
      return null;
    }
  }
}
