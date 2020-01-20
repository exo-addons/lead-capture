package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.*;

@Data
public class LeadCaptureSettings implements Serializable, Cloneable {

  private static final long       serialVersionUID = -5725443183560646198L;

  private Integer                 dataVersion      = 0;

  private String                  marketingSpace    = null;

  private String                  marketingGroup    = null;

  private String                  marketingBotUserName  = null;

  private String                  leadTaskProject  = null;

  private String                  senderMail  = null;

  private String                  leadManagementServerUrl  = null;

  private String                  leadCaptureToken  = null;

  private String                  leadManagementToken  = null;

  private boolean                 mailingEnabled;

  private boolean                 captureEnabled;

  private boolean                 leadManagementServer;


  public LeadCaptureSettings clone() { // NOSONAR
    try {
      return (LeadCaptureSettings) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Error while cloning object: " + this, e);
    }
  }

}
