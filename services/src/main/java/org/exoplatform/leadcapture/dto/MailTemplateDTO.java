package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import lombok.Data;
import org.exoplatform.leadcapture.entity.MailContentEntity;

@Data
public class MailTemplateDTO implements Serializable {
  private Long           Id;

  private  String name;

  private  String description;

  private List<MailContentDTO> contents;

}