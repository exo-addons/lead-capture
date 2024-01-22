package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "MailContentEntity")
@ExoEntity
@Table(name = "ADDONS_LC_MAIL_CONTENT")
@Data
@NamedQueries({
    @NamedQuery(name = "MailContentEntity.getContentByTemplate", query = "SELECT content FROM MailContentEntity content where content.mailTemplateEntity.id = :templateId ") })

public class MailContentEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_MAIL_CONTENT_ID", sequenceName = "SEQ_ADDONS_LC_MAIL_CONTENT_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_MAIL_CONTENT_ID")
  @Column(name = "ID")
  protected Long             id;

  @Column(name = "LANGUAGE", nullable = false)
  protected String           language;

  @Column(name = "SUBJECT")
  protected String           subject;

  @Column(name = "CONTENT")
  protected String           content;

  @ManyToOne
  @JoinColumn(name = "LC_MAIL_TEMPLATE_ID")
  private MailTemplateEntity mailTemplateEntity;

  public MailContentEntity() {
  }

  public MailContentEntity(String language, String subject, String content, MailTemplateEntity mailTemplateEntity) {
    this.language = language;
    this.subject = subject;
    this.content = content;
    this.mailTemplateEntity = mailTemplateEntity;
  }

}
