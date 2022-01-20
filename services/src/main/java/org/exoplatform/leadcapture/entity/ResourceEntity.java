package org.exoplatform.leadcapture.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.exoplatform.commons.api.persistence.ExoEntity;

import lombok.Data;

@Entity(name = "ResourceEntity")
@ExoEntity
@Table(name = "ADDONS_LC_RESOURCE")
@Data

public class ResourceEntity implements Serializable {

  @Id
  @SequenceGenerator(name = "SEQ_ADDONS_LC_RESOURCE_ID", sequenceName = "SEQ_ADDONS_LC_RESOURCE_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ADDONS_LC_RESOURCE_ID")
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
