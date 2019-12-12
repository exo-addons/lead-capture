package org.exoplatform.leadcapture.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.FieldEntity;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class FieldDAO extends GenericDAOJPAImpl<FieldEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(FieldDAO.class);

}
