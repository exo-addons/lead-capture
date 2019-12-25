package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "MailTemplateEntity")
@ExoEntity
@Table(name = "ADDONS_LC_MAIL_TEMPLATE")
@Data
@NamedQueries({
    @NamedQuery(name = "MailTemplateEntity.getTemplatesbyEvent", query = "SELECT template FROM MailTemplateEntity template where template.event = :event ") })

public class MailTemplateEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_MAIL_TEMPLATE_ID", sequenceName = "SEQ_ADDONS_LC_MAIL_TEMPLATE_ID")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_MAIL_TEMPLATE_ID")
  @Column(name = "ID")
  protected Long   id;

  @Column(name = "NAME", nullable = false)
  protected String name;

  @Column(name = "DESCRIPTION")
  protected String description;

  @Column(name = "EVENT")
  protected String event;

  @Column(name = "FORM")
  protected String form;

  @Column(name = "FIELD")
  protected String field;

  /*
   * @OneToMany(orphanRemoval=true)
   * @JoinColumn(name = "ID") private Collection<MailContentEntity> contents;
   */

  public MailTemplateEntity() {
  }

  public MailTemplateEntity(String name, String description) {
    this.name = name;
    this.description = description;
  }

}
