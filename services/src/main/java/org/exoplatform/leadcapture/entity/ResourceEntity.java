package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import jakarta.persistence.*;

import io.meeds.common.persistence.PortableSequence;

import lombok.Data;

@Entity(name = "ResourceEntity")
@Table(name = "ADDONS_LC_RESOURCE")
@Data

public class ResourceEntity implements Serializable {

  @Id
  @PortableSequence(name = "SEQ_ADDONS_LC_RESOURCE_ID")
  @Column(name = "ID")
  protected Long   id;

  @Column(name = "NAME", nullable = false)
  protected String name;

  @Column(name = "TYPE", nullable = false)
  protected String type;

  @Column(name = "PATH", nullable = false)
  protected String path;

  @Column(name = "URL", nullable = false)
  protected String url;

  public ResourceEntity() {
  }

  public ResourceEntity(String name, String type, String path, String url) {
    this.name = name;
    this.type = type;
    this.path = path;
    this.url = url;
  }

}
