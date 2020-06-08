package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class CompaignDTO implements Serializable {
  private Long   Id;

  private String name;

  private String field;

  private String value;

  private Boolean enabled;

  private int after;

  private MailTemplateDTO mailTemplateDTO;

}
