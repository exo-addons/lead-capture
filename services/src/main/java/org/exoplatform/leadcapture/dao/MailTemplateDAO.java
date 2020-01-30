package org.exoplatform.leadcapture.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.MailContentEntity;
import org.exoplatform.leadcapture.entity.MailTemplateEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class MailTemplateDAO extends GenericDAOJPAImpl<MailTemplateEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(MailTemplateDAO.class);

  public List<MailTemplateEntity> getTemplatesbyEvent(String event) {

    TypedQuery<MailTemplateEntity> query = getEntityManager()
                                                             .createNamedQuery("MailTemplateEntity.getTemplatesbyEvent",
                                                                               MailTemplateEntity.class)
                                                             .setParameter("event", event);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<MailTemplateEntity>();
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of mail templates by event {}", event, e);
      return new ArrayList<MailTemplateEntity>();
    }
  }

}
