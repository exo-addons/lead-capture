package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MailTemplateDTO implements Serializable {
  private Long                 Id;

  private String               name;

  private String               description;

  private String               event;

  private String               form;

  private String               field;

  private List<MailContentDTO> contents;

}
