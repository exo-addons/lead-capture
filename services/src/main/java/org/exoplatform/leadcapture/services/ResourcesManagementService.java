package org.exoplatform.leadcapture.services;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.leadcapture.dao.ResourceDAO;
import org.exoplatform.leadcapture.dto.ResourceDTO;
import org.exoplatform.leadcapture.entity.ResourceEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

public class ResourcesManagementService {

  private final Log   LOG = ExoLogger.getLogger(ResourcesManagementService.class);

  private ResourceDAO resourceDAO;

  public ResourcesManagementService(ResourceDAO resourceDAO) {
    this.resourceDAO = resourceDAO;
  }

  public List<ResourceDTO> getResources() {
    List<ResourceDTO> resourceDTOList = new ArrayList<>();
    List<ResourceEntity> resourceEntities = resourceDAO.findAll();
    if (resourceEntities != null) {
      for (ResourceEntity resourceEntity : resourceEntities) {
        if (resourceEntity != null) {
          resourceDTOList.add(toResourceDTO(resourceEntity));
        }
      }
    }
    return resourceDTOList;
  }

  public ResourceDTO addResource(ResourceDTO resourceDTO) {
    return toResourceDTO(resourceDAO.create(toResourceEntity(resourceDTO)));
  }

  public void deleteResource(ResourceEntity resourceEntityEntity) {
    resourceDAO.delete(resourceEntityEntity);
  }

  public ResourceDTO updateResource(ResourceDTO resourceDTO) {
    ResourceEntity newRessource = resourceDAO.update(toResourceEntity(resourceDTO));
    return toResourceDTO(newRessource);

  }

  public ResourceEntity getResourceById(Long id) {
    return resourceDAO.find(id);

  }

  public String getResourceUrlByPath(String path) {
    List<ResourceEntity> resourceEntities = resourceDAO.findAll();
    for (ResourceEntity resourceEntity : resourceEntities) {
      if (resourceEntity.getPath().contains(path) || path.contains(resourceEntity.getPath())) {
        return resourceEntity.getUrl();
      }
    }
    return null;
  }

  public List<ResourceDTO> toResourceDtoList(List<ResourceEntity> resourceEntities) {
    List<ResourceDTO> resourceDTOS = new ArrayList<>();
    for (ResourceEntity resourceEntity : resourceEntities) {
      resourceDTOS.add(toResourceDTO(resourceEntity));
    }
    return resourceDTOS;
  }

  public ResourceDTO toResourceDTO(ResourceEntity resourceEntity) {
    ResourceDTO resourceDTO = new ResourceDTO();
    resourceDTO.setId(resourceEntity.getId());
    resourceDTO.setName(resourceEntity.getName());
    resourceDTO.setType(resourceEntity.getType());
    resourceDTO.setPath(resourceEntity.getPath());
    resourceDTO.setUrl(resourceEntity.getUrl());
    return resourceDTO;
  }

  public ResourceEntity toResourceEntity(ResourceDTO resourceDTO) {
    ResourceEntity resourceEntity = new ResourceEntity();
    resourceEntity.setId(resourceDTO.getId());
    resourceEntity.setName(resourceDTO.getName());
    resourceEntity.setType(resourceDTO.getType());
    resourceEntity.setPath(resourceDTO.getPath());
    resourceEntity.setUrl(resourceDTO.getUrl());
    return resourceEntity;
  }
}
