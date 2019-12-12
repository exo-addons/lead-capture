package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class FieldDTO implements Serializable {
  private Long    id;

  private String  name;

  private String  value;
}
