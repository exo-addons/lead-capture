package org.exoplatform.leadcapture.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class LeadDAO extends GenericDAOJPAImpl<LeadEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(LeadDAO.class);

  public List<LeadEntity> getLeads(String search,
                                   String status,
                                   String owner,
                                   String captureMethod,
                                   Boolean notassigned,
                                   int offset,
                                   int limit,
                                   String sortBy,
                                   boolean sortDesc) {

    try {
      String queryString = "SELECT lead FROM LeadEntity lead";
      if (StringUtils.isNotEmpty(search) || StringUtils.isNotEmpty(status) || StringUtils.isNotEmpty(owner)|| StringUtils.isNotEmpty(captureMethod)   ||  notassigned) {
        queryString = queryString + " where ";
        if (StringUtils.isNotEmpty(search)) {
          search = search.toLowerCase();
          queryString = queryString + " lower(lead.firstName) LIKE '%' || '" + search
              + "'|| '%' or lower(lead.lastName) LIKE '%' || '" + search + "' || '%' or lower(lead.mail) LIKE '%' || '" + search
              + "' || '%' or lower(lead.country) LIKE '%' || '" + search + "' || '%'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(status)) {
          queryString = queryString + " lead.status = '" + status + "'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(owner)) {
          queryString = queryString + " lead.assignee = '" + owner + "'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(captureMethod)) {
          queryString = queryString + " lead.captureMethod = '" + captureMethod + "'";
          queryString = queryString + " and ";
        }
        if (notassigned) {
          queryString = queryString + " lead.assignee is null";
        }
        if (queryString.endsWith(" and ")) {
          queryString = queryString.substring(0, queryString.length() - 5);
        }
      }
      if (StringUtils.isNotEmpty(sortBy)) {
        if (sortDesc) {
          queryString = queryString + " ORDER BY lead." + sortBy + " DESC";
        } else {
          queryString = queryString + " ORDER BY lead." + sortBy + " ASC";
        }
      }
      TypedQuery<LeadEntity> query = getEntityManager().createQuery(queryString, LeadEntity.class);
      if (offset >= 0 && limit > 0) {
        query.setFirstResult(offset).setMaxResults(limit);
      }
      return query.getResultList();
    } catch (Exception e) {
      LOG.warn("Exception while attempting to get scores with offset = '" + offset + "' and limit = '" + limit + "'.", e);
      throw e;
    }
  }

  public long countLeads(String search, String status, String owner, String captureMethod, Boolean notassigned) {
    try {
      String queryString = "SELECT count(lead.id) FROM  LeadEntity lead";
      if (StringUtils.isNotEmpty(search) || StringUtils.isNotEmpty(status) || StringUtils.isNotEmpty(owner) || StringUtils.isNotEmpty(captureMethod) || notassigned) {
        queryString = queryString + " where ";
        if (StringUtils.isNotEmpty(search)) {
          search = search.toLowerCase();
          queryString = queryString + " lower(lead.firstName) LIKE '%' || '" + search
              + "'|| '%' or lower(lead.lastName) LIKE '%' || '" + search + "' || '%' or lower(lead.mail) LIKE '%' || '" + search
              + "' || '%' or lower(lead.country) LIKE '%' || '" + search + "' || '%'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(status)) {
          queryString = queryString + " lead.status = '" + status + "'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(owner)) {
          queryString = queryString + " lead.assignee = '" + owner + "'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(captureMethod)) {
          queryString = queryString + " lead.captureMethod = '" + captureMethod + "'";
          queryString = queryString + " and ";
        }
        if (notassigned) {
          queryString = queryString + " lead.assignee is null";
        }
        if (queryString.endsWith(" and ")) {
          queryString = queryString.substring(0, queryString.length() - 5);
        }
      }
      return getEntityManager().createQuery(queryString, Long.class).getSingleResult();
    } catch (Exception e) {
      LOG.warn("Exception while attempting to get leads count.", e);
      throw e;
    }
  }

  public LeadEntity getLeadByMail(String mail) {

    TypedQuery<LeadEntity> query = getEntityManager().createNamedQuery("LeadEntity.getLeadByMail", LeadEntity.class)
                                                     .setParameter("mail", mail);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get lead by email {}", mail, e);
      return null;
    }
  }

  public LeadEntity getLeadByTask(Long taskId) {

    TypedQuery<LeadEntity> query = getEntityManager().createNamedQuery("LeadEntity.getLeadByTask", LeadEntity.class)
                                                     .setParameter("taskId", taskId);

    try {
      return query.getSingleResult();
    } catch (NoResultException e) {
      return null;
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get lead by taskId {}", taskId, e);
      return null;
    }
  }

}
