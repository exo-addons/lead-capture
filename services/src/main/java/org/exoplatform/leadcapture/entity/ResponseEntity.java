package org.exoplatform.leadcapture.entity;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "ResponseEntity")
@ExoEntity
@Table(name = "ADDONS_LC_RESPONSE")
@Data
 @NamedQueries({
  @NamedQuery(
          name = "ResponseEntity.getResponsesByForm",
          query = "SELECT response FROM ResponseEntity response where response.formEntity.id = :formId " )
 })

public class ResponseEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_RESPONSE_ID", sequenceName = "SEQ_ADDONS_LC_RESPONSE_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_RESPONSE_ID")
  @Column(name = "ID")
  protected Long    id;

  @ManyToOne
  @JoinColumn(name = "LC_FORM_ID", nullable = false)
  private FormEntity formEntity ;

  @ManyToOne
  @JoinColumn(name = "LC_LEAD_ID", nullable = false)
  private LeadEntity leadEntity ;

  @OneToMany
  @JoinColumn(name = "ID")
  private Collection<FieldEntity> filelds;

  @Column(name = "CREATED_DATE")
  protected Long    createdDate;

  public ResponseEntity() {}

  public ResponseEntity( FormEntity formEntity, LeadEntity leadEntity) {
    this.formEntity = formEntity;
    this.leadEntity = leadEntity;
  }

}
