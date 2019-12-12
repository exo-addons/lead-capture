package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class FormInfo implements Serializable {

  private LeadDTO  lead;

  private ResponseDTO response;


}
