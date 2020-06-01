package org.exoplatform.leadcapture.dao;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.CompaignEntity;
import org.exoplatform.leadcapture.entity.ResourceEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class CompaignDAO extends GenericDAOJPAImpl<CompaignEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(CompaignDAO.class);
}
