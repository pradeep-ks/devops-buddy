package in.devopsbuddy.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SMTPEmailService extends AbstractEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMTPEmailService.class);

    @Autowired
    private MailSender mailSender;

    @Override
    public void sendSimpleMailMessage(SimpleMailMessage message) {
        LOGGER.info("Sending acknowledgement to: {}", message.getTo().toString());
        this.mailSender.send(message);
        LOGGER.debug("Email sent!");
    }
    
}
