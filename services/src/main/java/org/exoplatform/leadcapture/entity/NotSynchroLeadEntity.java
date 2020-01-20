package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "NotSynchroLeadEntity")
@ExoEntity
@Table(name = "ADDONS_LC_NOT_SYNCHRO_LEAD")
@Data

@NamedQueries({
    @NamedQuery(name = "NotSynchroLeadEntity.getNotSynchLead", query = "SELECT lead FROM NotSynchroLeadEntity lead where lead.leadEntity.id = :leadId and lead.responseEntity.id = :responseId ") })

public class NotSynchroLeadEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_NOT_SYNCHRO_LEAD_ID", sequenceName = "SEQ_ADDONS_LC_NOT_SYNCHRO_LEAD_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_NOT_SYNCHRO_LEAD_ID")
  @Column(name = "ID")
  protected Long         id;

  @ManyToOne
  @JoinColumn(name = "LC_LEAD_ID")
  private LeadEntity     leadEntity;

  @ManyToOne
  @JoinColumn(name = "LC_RESPONSE_ID")
  private ResponseEntity responseEntity;

  @Column(name = "ATTEMPTS_NUMBER")
  protected int          atteptsNumber;

  public NotSynchroLeadEntity() {
  }

  public NotSynchroLeadEntity(LeadEntity leadEntity, ResponseEntity responseEntity, int atteptsNumber) {
    this.leadEntity = leadEntity;
    this.responseEntity = responseEntity;
    this.atteptsNumber = atteptsNumber;
  }

}
