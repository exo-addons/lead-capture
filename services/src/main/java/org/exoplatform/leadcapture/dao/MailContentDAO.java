package org.exoplatform.leadcapture.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class MailContentDAO extends GenericDAOJPAImpl<MailContentEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(MailContentDAO.class);

  public List<MailContentEntity> getContentByTemplate(long templateId) {

    TypedQuery<MailContentEntity> query = getEntityManager()
                                                            .createNamedQuery("MailContentEntity.getContentByTemplate",
                                                                              MailContentEntity.class)
                                                            .setParameter("templateId", templateId);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<MailContentEntity>();
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of mail content by formId {}", templateId, e);
      return new ArrayList<MailContentEntity>();
    }
  }
}
