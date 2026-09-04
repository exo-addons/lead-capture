package org.exoplatform.leadcapture.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

import io.meeds.common.persistence.PortableSequence;

import lombok.Data;
import org.exoplatform.task.domain.Comment;

@Entity(name = "ResponseEntity")
@Table(name = "ADDONS_LC_RESPONSE")
@Data
@NamedQueries({
    @NamedQuery(name = "ResponseEntity.getResponsesByFormAndLead", query = "SELECT response FROM ResponseEntity response where response.formEntity.id = :formId and response.leadEntity.id = :leadId"),
    @NamedQuery(name = "ResponseEntity.getResponsesByLead", query = "SELECT response FROM ResponseEntity response where response.leadEntity.id = :leadId") })

public class ResponseEntity implements Serializable {

  @Id
  @PortableSequence(name = "SEQ_ADDONS_LC_RESPONSE_ID")
  @Column(name = "ID")
  protected Long                  id;

  @Column(name = "CREATED_DATE")
  protected Date createdDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "LC_FORM_ID", nullable = false)
  private FormEntity              formEntity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "LC_LEAD_ID", nullable = false)
  private LeadEntity              leadEntity;


  @OneToMany(mappedBy = "responseEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
  private Collection<FieldEntity> filelds = new ArrayList<FieldEntity>();

  public ResponseEntity() {
  }

  public ResponseEntity(FormEntity formEntity, LeadEntity leadEntity) {
    this.formEntity = formEntity;
    this.leadEntity = leadEntity;
  }

}
