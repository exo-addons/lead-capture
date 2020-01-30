package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ResourceDTO implements Serializable {
  private Long   Id;

  private String name;

  private String type;

  private String path;

  private String url;

}
