package in.devopsbuddy.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import in.devopsbuddy.web.domain.dto.FeedbackDTO;
import in.devopsbuddy.web.service.EmailService;

@Controller
public class ContactController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

    private static final String CONTACT_VIEW_NAME = "contact/contact";
    
    private static final String CONTACT_MODEL_KEY = "feedbackDTO";

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contact(ModelMap map) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        map.addAttribute(CONTACT_MODEL_KEY, feedbackDTO);
        return CONTACT_VIEW_NAME;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contact(@ModelAttribute(CONTACT_MODEL_KEY) FeedbackDTO feedbackDTO) {
        LOGGER.info("Sending feedback: {}", feedbackDTO);
        this.emailService.sendFeedbackMail(feedbackDTO);
        return CONTACT_VIEW_NAME;
    }
}
