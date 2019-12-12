package org.exoplatform.leadcapture.dto;

import lombok.Data;
import org.exoplatform.leadcapture.entity.FormEntity;
import org.exoplatform.leadcapture.entity.ResponseEntity;

import java.io.Serializable;
import java.io.Serializable;
import java.util.List;
@Data
public class FormResponses implements Serializable {
    protected FormDTO formEntity;
    protected List<ResponseDTO> responses;

    public FormResponses(FormDTO formEntity, List<ResponseDTO> responses) {
        this.formEntity = formEntity;
        this.responses = responses;
    }
}
