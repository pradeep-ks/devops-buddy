package in.devopsbuddy.web.controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.i18n.I18NService;
import in.devopsbuddy.web.service.EmailService;
import in.devopsbuddy.web.service.PasswordResetTokenService;
import in.devopsbuddy.web.service.UserService;

@Controller
public class PasswordResetController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PasswordResetController.class);

    public static final String VIEW_NAME_FORGOT_PASSWORD_FORM = "forgotpassword/emailForm";

    public static final String VIEW_NAME_UPDATE_PASSWORD = "forgotpassword/updatePassword";

    public static final String URL_FORGOT_PASSWORD = "/forgotpassword";

    public static final String URL_UPDATE_PASSWORD = "/updatePassword";

    public static final String ATTRIBUTE_NAME_EMAIL_SENT = "emailSent";

    public static final String ATTRIBUTE_NAME_PASSWORD_RESET = "passwordReset";

    public static final String ATTRIBUTE_NAME_MESSAGE = "message";

    private static final String I18N_MESSAGE_KEY = "forgot-password.email.display-text";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Value("${webmaster.email}")
    private String webmasterEmail;

    @Value("${app.default.from.mail-address}")
    private String defaultFromEmail;

    @RequestMapping(value = URL_FORGOT_PASSWORD, method = RequestMethod.GET)
    public String forgotPassword() {
        return VIEW_NAME_FORGOT_PASSWORD_FORM;
    }

    @RequestMapping(value = URL_FORGOT_PASSWORD, method = RequestMethod.POST)
    public String forgotPassword(HttpServletRequest request, @RequestParam("email") String email, ModelMap modelMap) {
        var resetToken = this.passwordResetTokenService.generatePasswordResetTokenForEmail(email);
        if (resetToken == null) {
            LOGGER.warn("Could not sent password reset email as the email {} does not exists!", email);
        } else {
            var user = resetToken.getUser();
            var token = resetToken.getToken();
            var passwordResetUrl = UserUtil.createPasswordResetUrl(request, user.getId(), token);
            LOGGER.debug("Password reset url: {}", passwordResetUrl);
            var urlText = this.i18nService.getMessage(I18N_MESSAGE_KEY, request.getLocale());
            var mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("\"[DevOps Buddy] Reset Your Password\"");
            mailMessage.setText(urlText + "\r\n" + passwordResetUrl);
            mailMessage.setFrom(defaultFromEmail);

            this.emailService.sendSimpleMailMessage(mailMessage);
        }
        modelMap.addAttribute(ATTRIBUTE_NAME_EMAIL_SENT, "true");
        return VIEW_NAME_FORGOT_PASSWORD_FORM;
    }

    @RequestMapping(value = URL_UPDATE_PASSWORD, method = RequestMethod.GET)
    public String updatePassword(@RequestParam("id") long id, @RequestParam("token") String token, Locale locale,
            ModelMap modelMap) {
        if (!StringUtils.hasLength(token) || id == 0L) {
            LOGGER.error("Invalid user id {} or token value {}", id, token);
            modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "false");
            modelMap.addAttribute(ATTRIBUTE_NAME_MESSAGE, "Invalid user id or token value");
            return VIEW_NAME_UPDATE_PASSWORD;
        }

        var passwordResetToken = this.passwordResetTokenService.findByToken(token);
        if (null == passwordResetToken) {
            LOGGER.warn("Token not found with value {}", token);
            modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "false");
            modelMap.addAttribute(ATTRIBUTE_NAME_MESSAGE, "Token not found!");
            return VIEW_NAME_UPDATE_PASSWORD;
        }

        var user = passwordResetToken.getUser();
        if (user.getId() != id) {
            LOGGER.error("The user id {} does not match with the user id {} associated with token {}", id, user.getId(),
                    token);
            modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "false");
            modelMap.addAttribute(ATTRIBUTE_NAME_MESSAGE,
                    this.i18nService.getMessage("password.reset.token.invalid", locale));
            return VIEW_NAME_UPDATE_PASSWORD;
        }

        if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
            LOGGER.error("The token {} has exipred", token);
            modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "false");
            modelMap.addAttribute(ATTRIBUTE_NAME_MESSAGE,
                    this.i18nService.getMessage("password.reset.token.expired", locale));
            return VIEW_NAME_UPDATE_PASSWORD;
        }

        modelMap.addAttribute("principalId", user.getId());
        // Ok to proceed. Auto-authenticate the user so that in the POST request we can
        // check if the user is authenticated
        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        return VIEW_NAME_UPDATE_PASSWORD;
    }

    @RequestMapping(value = URL_UPDATE_PASSWORD, method = RequestMethod.GET)
    public String updatePassword(@RequestParam("principal_id") long id, @RequestParam("password") String password,
            ModelMap modelMap) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            LOGGER.error("An unauthenticated user tried to invoke the reset password POST method.");
            modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "false");
            modelMap.addAttribute(ATTRIBUTE_NAME_MESSAGE, "You're not authorized to perform this action!");
            return VIEW_NAME_UPDATE_PASSWORD;
        }

        var user = (User) authentication.getPrincipal();
        if (user.getId() != id) {
            LOGGER.error("Security Breach!! User {} is trying to make a password reset on behalf of user {}", user.getId(), id);
            modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "false");
            modelMap.addAttribute(ATTRIBUTE_NAME_MESSAGE, "You're not authorized to perform this action!");
            return VIEW_NAME_UPDATE_PASSWORD;
        }

        this.userService.updatePassword(id, password); 
        LOGGER.info("Password successfully updated for user {}", user.getUsername());
        modelMap.addAttribute(ATTRIBUTE_NAME_PASSWORD_RESET, "true");
        return VIEW_NAME_UPDATE_PASSWORD;
    }
}
