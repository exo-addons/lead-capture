package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "FormEntity")
@ExoEntity
@Table(name = "ADDONS_LC_FORM")
@Data
@NamedQueries({
    @NamedQuery(name = "FormEntity.getFormByName", query = "SELECT form FROM FormEntity form where form.name = :name") })

public class FormEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_FORM_ID", sequenceName = "SEQ_ADDONS_LC_FORM_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_FORM_ID")
  @Column(name = "ID")
  protected Long   id;

  @Column(name = "NAME", nullable = false, unique = true)
  protected String name;

  @Column(name = "FIELDS")
  protected String fields;

  public FormEntity() {
  }

  public FormEntity(String name, String fields) {
    this.name = name;
    this.fields = fields;
  }

}
