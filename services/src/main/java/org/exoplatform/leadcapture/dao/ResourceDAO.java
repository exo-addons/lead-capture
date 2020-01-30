package org.exoplatform.leadcapture.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.ResourceEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ResourceDAO extends GenericDAOJPAImpl<ResourceEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(ResourceDAO.class);
}
