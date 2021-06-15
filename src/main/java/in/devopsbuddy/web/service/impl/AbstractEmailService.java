package in.devopsbuddy.web.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import in.devopsbuddy.web.domain.dto.FeedbackDTO;
import in.devopsbuddy.web.service.EmailService;

public abstract class AbstractEmailService implements EmailService {

    @Value("${app.default.from.mail-address}")
    private String defaultFromAddress;

    protected SimpleMailMessage toSimpleMailMessage(FeedbackDTO feedbackDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(defaultFromAddress);
        message.setTo(feedbackDTO.getEmail());
        message.setSubject("[Acknowledgement] Thank you for your feedback!");
        message.setText(String.format("Thank you for your feedback '%s %s'. Following is your feedback:\n'%s'",
                feedbackDTO.getFirstName(), feedbackDTO.getLastName(), feedbackDTO.getFeedback()));
        return message;
    }

    @Override
    public void sendFeedbackMail(FeedbackDTO feedbackDTO) {
        sendSimpleMailMessage(toSimpleMailMessage(feedbackDTO));
    }
}
