package org.exoplatform.leadcapture.dto;

import static org.exoplatform.leadcapture.Utils.taskFormatter;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PersonalTask implements Serializable {

  public PersonalTask() {
  }

  public PersonalTask(Long id, LeadDTO lead, String userId, String title, Date dueDate, boolean completed) {
    this.lead = lead;
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.dueDate = dueDate;
    this.completed = completed;
    this.formattedDueDate = taskFormatter.format(dueDate);
  }

  private Long    id               = null;

  private LeadDTO lead             = null;

  private String  userId           = null;

  private String  title            = null;

  private Date    dueDate          = null;

  private String  formattedDueDate = null;

  private boolean completed = false;

}
