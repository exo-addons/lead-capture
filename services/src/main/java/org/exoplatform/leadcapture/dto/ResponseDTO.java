package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ResponseDTO implements Serializable {
  private Long    Id;

  private FormDTO  form;

  private String  formName;

  private List<FieldDTO>  fields;

  private Long    createdDate;
}
