package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FormResponses implements Serializable {
  protected FormDTO           formEntity;

  protected List<ResponseDTO> responses;

  public FormResponses(FormDTO formEntity, List<ResponseDTO> responses) {
    this.formEntity = formEntity;
    this.responses = responses;
  }
}
