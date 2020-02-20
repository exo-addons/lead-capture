package org.exoplatform.leadcapture.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class LeadCaptureSettings implements Serializable, Cloneable {

  private static final long serialVersionUID           = -5725443183560646198L;

  private Integer           dataVersion                = 0;

  private String            userExperienceSpace        = null;

  private String            userExperienceGroup        = null;

  private String            userExperienceBotUserName  = null;

  private String            leadTaskProject            = null;

  private String            senderMail                 = null;

  private boolean           mailingEnabled             = false;

  private boolean           captureEnabled             = false;

  private String            allowedCaptureSourceDomain = null;

  private String            captureToken               = null;

  private String            resourcesIdentifier        = null;

  private String            mailsBlackList             = null;

  private String            autoOpeningForms           = null;

  private String            unsubscribeUrl             = null;

  private String            leadManagementAppUrl       = null;

  public LeadCaptureSettings clone() { // NOSONAR
    try {
      return (LeadCaptureSettings) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new IllegalStateException("Error while cloning object: " + this, e);
    }
  }

}
