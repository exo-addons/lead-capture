package org.exoplatform.leadcapture.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FormDTO implements Serializable {

  private Long    id;

  private String  name;

  private String  fields;

}
