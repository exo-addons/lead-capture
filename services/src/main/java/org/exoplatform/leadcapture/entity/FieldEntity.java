package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "FieldEntity")
@ExoEntity
@Table(name = "ADDONS_LC_FORM_FIELD")
@Data
@NamedQueries({
    @NamedQuery(name = "FieldEntity.getFieldsByResponse", query = "SELECT field FROM FieldEntity field where field.responseEntity.id = :responseId ") })

public class FieldEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_FORM_FIELD_ID", sequenceName = "SEQ_ADDONS_LC_FORM_FIELD_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_FORM_FIELD_ID")
  @Column(name = "ID")
  protected Long         id;

  @Column(name = "NAME", nullable = false, unique = true)
  protected String       name;

  @Column(name = "VALUE")
  protected String       value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "LC_RESPONSE_ID")
  private ResponseEntity responseEntity;

  public FieldEntity() {
  }

  public FieldEntity(String name, String value, ResponseEntity responseEntity) {
    this.name = name;
    this.value = value;
    this.responseEntity = responseEntity;
  }

}
