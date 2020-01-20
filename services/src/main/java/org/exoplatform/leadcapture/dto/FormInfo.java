package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FormInfo implements Serializable {

  private Long id;

  private LeadDTO     lead;

  private ResponseDTO response;

  public FormInfo(Long id, LeadDTO lead, ResponseDTO response) {
    this.id = id;
    this.lead = lead;
    this.response = response;
  }
  public FormInfo() {

  }

}
