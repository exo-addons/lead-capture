package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class LeadsAccessList implements Serializable {

  public LeadsAccessList() {
  }

  public LeadsAccessList(List<LeadDTO> leads, Long size) {
    this.leads = leads;
    this.size = size;
  }

  private List<LeadDTO> leads;

  private Long          size;

}
