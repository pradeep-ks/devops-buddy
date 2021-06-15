package in.devopsbuddy.web.service;

import org.springframework.mail.SimpleMailMessage;

import in.devopsbuddy.web.domain.dto.FeedbackDTO;

public interface EmailService {
    
    void sendFeedbackMail(FeedbackDTO feedbackDTO);

    void sendSimpleMailMessage(SimpleMailMessage message);
    
}
