package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "CompaignEntity")
@ExoEntity
@Table(name = "ADDONS_LC_COMPAIGN")
@Data

public class CompaignEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_COMPAIGN_ID", sequenceName = "SEQ_ADDONS_LC_COMPAIGN_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_COMPAIGN_ID")
  @Column(name = "ID")
  protected Long   id;

  @Column(name = "NAME", nullable = false)
  protected String name;

  @Column(name = "FIELD", nullable = false)
  protected String field;

  @Column(name = "VALUE", nullable = false)
  protected String value;

  @Column(name = "ENABLED")
  protected Boolean enabled;

  @Column(name = "AFTER")
  protected int after;

  @ManyToOne
  @JoinColumn(name = "LC_MAIL_TEMPLATE_ID")
  private MailTemplateEntity mailTemplateEntity;

  public CompaignEntity() {
  }

  public CompaignEntity(String name, String field, String value, Boolean enabled, int after, MailTemplateEntity mailTemplateEntity) {
    this.name = name;
    this.field = field;
    this.value = value;
    this.enabled = enabled;
    this.after = after;
    this.mailTemplateEntity = mailTemplateEntity;
  }

}
