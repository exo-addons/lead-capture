package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class MailContentDTO implements Serializable {
  private Long            id;

  private String          language;

  private String          subject;

  private String          content;

  private MailTemplateDTO mailTemplateDTO;
}
