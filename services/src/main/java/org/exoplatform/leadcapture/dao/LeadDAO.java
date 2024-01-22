package org.exoplatform.leadcapture.dao;

import static org.exoplatform.leadcapture.Utils.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import org.exoplatform.commons.persistence.impl.GenericDAOJPAImpl;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class LeadDAO extends GenericDAOJPAImpl<LeadEntity, Long> {

  private static final Log LOG = ExoLogger.getLogger(LeadDAO.class);

  public List<LeadEntity> getLeads(String search,
                                   String status,
                                   String owner,
                                   String captureMethod,
                                   String from,
                                   String to,
                                   String zone,
                                   int min,
                                   int max,
                                   Boolean notassigned,
                                   int offset,
                                   int limit,
                                   String sortBy,
                                   boolean sortDesc) {

    try {
      String queryString = "SELECT lead FROM LeadEntity lead";
      if (StringUtils.isNotEmpty(search) || StringUtils.isNotEmpty(status) || StringUtils.isNotEmpty(owner)
          || StringUtils.isNotEmpty(captureMethod) || StringUtils.isNotEmpty(from) || StringUtils.isNotEmpty(to)
          || StringUtils.isNotEmpty(zone) || notassigned) {
        queryString = queryString + " where ";
        if (StringUtils.isNotEmpty(search)) {
          search = search.toLowerCase();
          queryString = queryString + " lower(lead.firstName) LIKE '%' || '" + search
              + "'|| '%' or lower(lead.lastName) LIKE '%' || '" + search + "' || '%' or lower(lead.mail) LIKE '%' || '" + search
              + "' || '%' or lower(lead.country) LIKE '%' || '" + search + "' || '%' or lower(lead.company) LIKE '%' || '"
              + search + "' || '%'";

          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(status)) {
          if (status.equals(ACTIVE_FILTER)) {

            queryString = queryString + " lead.status NOT IN (" + getStatusList(LEAD_INACTIVE_STATUSES) + ")";
            queryString = queryString + " and ";
          } else {
            queryString = queryString + " lead.status = '" + status + "'";
            queryString = queryString + " and ";
          }
        }
        if (StringUtils.isNotEmpty(owner)) {
          queryString = queryString + " lead.assignee = '" + owner + "'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(captureMethod)) {
          queryString = queryString + " lead.captureMethod = '" + captureMethod + "'";
          queryString = queryString + " and ";
        }

        if (StringUtils.isNotEmpty(zone)) {
          queryString = queryString + " lead.geographiqueZone = '" + zone + "'";
          queryString = queryString + " and ";
        }

        if (min > 0) {
          queryString = queryString + " lead.usersNumber >= '" + min + "'";
          queryString = queryString + " and ";
        }

        if (max > 0) {
          queryString = queryString + " lead.usersNumber <= '" + max + "'";
          queryString = queryString + " and ";
        }

        if (StringUtils.isNotEmpty(from)) {
          try {
            long fromDate = taskFormatter.parse(from).getTime();
            String date = quryDateFormatter.format(fromDate);
            queryString = queryString + " TIMESTAMP(lead.createdDate) >= '" + date + "'";
            queryString = queryString + " and ";
          } catch (Exception e) {
            LOG.error("Cannot parse from date, the from date filer will not applied to get th list of leads");
          }
        }
        if (StringUtils.isNotEmpty(to)) {
          try {
            Date toDate = taskFormatter.parse(to);
            Calendar cal = Calendar.getInstance();
            cal.setTime(toDate);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            String date = quryDateFormatter.format(cal.getTime());
            queryString = queryString + " TIMESTAMP(lead.createdDate) <= '" + date + "'";
            queryString = queryString + " and ";
          } catch (Exception e) {
            LOG.error("Cannot parse from date, the to date filer will not applied to get th list of leads");
          }
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
      } else {
        queryString = queryString + " ORDER BY lead.id DESC";
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

  public long countLeads(String search,
                         String status,
                         String owner,
                         String captureMethod,
                         String from,
                         String to,
                         String zone,
                         int min,
                         int max,
                         Boolean notassigned) {
    try {
      String queryString = "SELECT count(lead.id) FROM  LeadEntity lead";
      if (StringUtils.isNotEmpty(search) || StringUtils.isNotEmpty(status) || StringUtils.isNotEmpty(owner)
          || StringUtils.isNotEmpty(captureMethod) || StringUtils.isNotEmpty(from) || StringUtils.isNotEmpty(to)
          || StringUtils.isNotEmpty(zone) || notassigned) {
        queryString = queryString + " where ";
        if (StringUtils.isNotEmpty(search)) {
          search = search.toLowerCase();
          queryString = queryString + " lower(lead.firstName) LIKE '%' || '" + search
              + "'|| '%' or lower(lead.lastName) LIKE '%' || '" + search + "' || '%' or lower(lead.mail) LIKE '%' || '" + search
              + "' || '%' or lower(lead.country) LIKE '%' || '" + search + "' || '%' or lower(lead.company) LIKE '%' || '"
              + search + "' || '%'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(status)) {
          if (status.equals(ACTIVE_FILTER)) {

            queryString = queryString + " lead.status NOT IN (" + getStatusList(LEAD_INACTIVE_STATUSES) + ")";
            queryString = queryString + " and ";
          } else {
            queryString = queryString + " lead.status = '" + status + "'";
            queryString = queryString + " and ";
          }
        }
        if (StringUtils.isNotEmpty(owner)) {
          queryString = queryString + " lead.assignee = '" + owner + "'";
          queryString = queryString + " and ";
        }
        if (StringUtils.isNotEmpty(captureMethod)) {
          queryString = queryString + " lead.captureMethod = '" + captureMethod + "'";
          queryString = queryString + " and ";
        }

        if (StringUtils.isNotEmpty(zone)) {
          queryString = queryString + " lead.geographiqueZone = '" + zone + "'";
          queryString = queryString + " and ";
        }

        if (min > 0) {
          queryString = queryString + " lead.usersNumber >= '" + min + "'";
          queryString = queryString + " and ";
        }

        if (max > 0) {
          queryString = queryString + " lead.usersNumber <= '" + max + "'";
          queryString = queryString + " and ";
        }

        if (StringUtils.isNotEmpty(from)) {
          try {
            long fromDate = taskFormatter.parse(from).getTime();
            String date = quryDateFormatter.format(fromDate);
            queryString = queryString + " TIMESTAMP(lead.createdDate) >= '" + date + "'";
            queryString = queryString + " and ";
          } catch (Exception e) {
            LOG.error("Cannot parse from date, the from date filer will not applied to get th list of leads");
          }
        }
        if (StringUtils.isNotEmpty(to)) {
          try {
            Date toDate = taskFormatter.parse(to);
            Calendar cal = Calendar.getInstance();
            cal.setTime(toDate);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            String date = quryDateFormatter.format(cal.getTime());
            queryString = queryString + " TIMESTAMP(lead.createdDate) <= '" + date + "'";
            queryString = queryString + " and ";
          } catch (Exception e) {
            LOG.error("Cannot parse from date, the to date filer will not applied to get th list of leads");
          }
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
  public List<LeadEntity> getLeadsByStatus(String status) {

    TypedQuery<LeadEntity> query = getEntityManager().createNamedQuery("LeadEntity.getLeadsByStatus", LeadEntity.class)
                                                     .setParameter("status", status);

    try {
      return query.getResultList();
    } catch (NoResultException e) {
      return new ArrayList<LeadEntity>();
    } catch (Exception e) {
      LOG.error("Error occurred when trying to get list of leads by status {}", status, e);
      return new ArrayList<LeadEntity>();
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

  private String getStatusList(String[] statuses) {
    String statusList = "";
    for (String status : statuses) {
      statusList += "'" + status + "',";
    }
    if (statusList.endsWith(",")) {
      statusList = statusList.substring(0, statusList.length() - 1);
    }
    return statusList;
  }

}
