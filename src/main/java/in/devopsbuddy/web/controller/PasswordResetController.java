package in.devopsbuddy.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.i18n.I18NService;
import in.devopsbuddy.web.service.EmailService;
import in.devopsbuddy.web.service.PasswordResetTokenService;

@Controller
public class PasswordResetController {
    
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PasswordResetController.class);

    private static final String USER_EMAIL_VIEW = "forgotpassword/emailForm";

    public static final String FORGOT_PASSWORD_URL = "/forgotpassword";

    public static final String MAIL_SENT_KEY = "emailSent";

    public static final String PASSWORD_RESET_URL = "/updatePassword";

    private static final String PASSWORD_RESET_URL_TEXT_PROPERTY_NAME = "forgot-password.email.display-text";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private EmailService emailService;

    @Value("${webmaster.email}")
    private String webmasterEmail;

    @Value("${app.default.from.mail-address}")
    private String defaultFromEmail;

    @RequestMapping(value = FORGOT_PASSWORD_URL, method = RequestMethod.GET)
    public String forgotPassword() {
        return USER_EMAIL_VIEW;
    }

    @RequestMapping(value = FORGOT_PASSWORD_URL, method = RequestMethod.POST)
    public String forgotPassword(HttpServletRequest request, @RequestParam("email") String email, ModelMap modelMap) {
        var resetToken = this.passwordResetTokenService.generatePasswordResetTokenForEmail(email);
        if (resetToken == null) {
            LOGGER.warn("Could not sent password reset email as the email {} does not exists!", email);
        } else {
            var user = resetToken.getUser();
            var token = resetToken.getToken();
            var passwordResetUrl = UserUtil.createPasswordResetUrl(request, user.getId(), token);
            LOGGER.debug("Password reset url: {}", passwordResetUrl);
            var urlText = this.i18nService.getMessage(PASSWORD_RESET_URL_TEXT_PROPERTY_NAME, request.getLocale());
            var mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("\"[DevOps Buddy] Reset Your Password\"");
            mailMessage.setText(urlText + "\r\n" + passwordResetUrl);
            mailMessage.setFrom(defaultFromEmail);

            this.emailService.sendSimpleMailMessage(mailMessage);
        }
        modelMap.addAttribute(MAIL_SENT_KEY, "true");
        return USER_EMAIL_VIEW;
    }
}
