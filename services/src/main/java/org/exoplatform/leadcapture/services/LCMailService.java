package org.exoplatform.leadcapture.services;

import static org.exoplatform.leadcapture.Utils.*;

import org.apache.commons.lang.StringUtils;
import org.exoplatform.commons.utils.StringCommonUtils;
import org.exoplatform.leadcapture.dao.LeadDAO;
import org.exoplatform.leadcapture.dto.LeadCaptureSettings;
import org.exoplatform.leadcapture.entity.LeadEntity;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.mail.MailService;
import org.exoplatform.services.mail.Message;


public class LCMailService {

  private static final Log   LOG       = ExoLogger.getLogger(LCMailService.class);
  private LeadDAO            leadDAO;
  private MailService        mailService;
  private LeadCaptureSettingsService        leadCaptureSettingsService;

  public LCMailService(LeadDAO leadDAO, MailService mailService, LeadCaptureSettingsService leadCaptureSettingsService) {
    this.leadDAO = leadDAO;
    this.mailService = mailService;
    this.leadCaptureSettingsService = leadCaptureSettingsService;
  }

  public static String convertCodeHTML(String s) {
    if (StringUtils.isEmpty(s))
      return EMPTY_STR;
    s = s.replaceAll("(<p>((\\&nbsp;)*)(\\s*)?</p>)|(<p>((\\&nbsp;)*)?(\\s*)</p>)", "<br/>").trim();
    s = s.replaceFirst("(<br/>)*", EMPTY_STR);
    s = s.replaceAll("(\\w|\\$)(>?,?\\.?\\*?\\!?\\&?\\%?\\]?\\)?\\}?)(<br/><br/>)*", "$1$2");
    try {
      s = s.replaceAll("(https?|ftp)://", " $0")
           .replaceAll("(=\"|='|'>|\">)( )(https?|ftp)", "$1$3")
           .replaceAll("[^=\"|^='|^'>|^\">](https?://|ftp://)([-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])",
                       "<a target=\"_blank\" href=\"$1$2\">$1$2</a>");
      s = s.replaceAll("&apos;", "'");
    } catch (Exception e) {
      LOG.error("Failed to convert HTML" + e.getMessage());
    }
    return s;
  }

  public void sendMail(String content, String subject, LeadEntity lead) throws Exception {
    LeadCaptureSettings settings = leadCaptureSettingsService.getSettings();
    if(settings.isMailingEnabled()) {
      if(settings.getSenderMail()!=null){
        Message message = new Message();
        message.setFrom(settings.getSenderMail());
        message.setTo(lead.getMail());
        message.setMimeType("text/html");
        message.setSubject(StringCommonUtils.decodeSpecialCharToHTMLnumber(subject));
        message.setBody(getContentEmail(content, lead));
        mailService.sendMessage(message);
      }else{
        LOG.warn("Mail sender adress is not defined, cannot send mail to {}",lead.getMail());
      }

    }else{
      LOG.warn("Lead Messaging is disabled, cannot send mail to {}",lead.getMail());
    }

  }

  public String getContentEmail(String content, LeadEntity lead) {

    String content_ = StringUtils.replace(content, "$FIRST_NAME", lead.getFirstName());
    content_ = StringUtils.replace(content_, "$LAST_NAME", lead.getLastName());
    content_ = StringUtils.replace(content_, "$MAIL", lead.getMail());
    return convertCodeHTML(content_);
  }

}
