package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.*;

@Data
public class LeadCaptureSettings implements Serializable, Cloneable {

  private static final long       serialVersionUID = -5725443183560646198L;

  private Integer                 dataVersion      = 0;

  private String                  userExperienceSpace    = null;

  private String                  userExperienceGroup    = null;

  private String                  userExperienceBotUserName  = null;

  private String                  leadTaskProject  = null;

  private String                  senderMail  = null;

  private String                  leadCaptureToken  = null;

  private boolean                 mailingEnabled;

  private boolean                 captureEnabled;

  private String                 allowedCaptureSourceDomain;

  public LeadCaptureSettings clone() { // NOSONAR
    try {
      return (LeadCaptureSettings) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Error while cloning object: " + this, e);
    }
  }

}
