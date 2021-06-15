package in.devopsbuddy.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;

public class MockEmailService extends AbstractEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MockEmailService.class);

    @Override
    public void sendSimpleMailMessage(SimpleMailMessage message) {
        LOGGER.info("Simulating email service....");
        LOGGER.info(message.toString());
        LOGGER.debug("Email sent!");
    }
    

}
