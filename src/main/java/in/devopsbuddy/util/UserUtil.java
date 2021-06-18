package in.devopsbuddy.util;

import javax.servlet.http.HttpServletRequest;

import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.web.controller.PasswordResetController;
import in.devopsbuddy.web.domain.dto.BasicAccount;

public class UserUtil {

    private UserUtil() {
        throw new AssertionError("Non instantiable");
    }

    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setCountry("India");
        user.setDescription("A dummy user for testing integration");
        user.setEmail(email);
        user.setEnabled(true);
        user.setFirstName("Dummy");
        user.setLastName("User");
        user.setPassword("password");
        user.setPhoneNumber("7982176473");
        user.setProfileImageUrl("https://www.google.co.in?q=dummy");
        user.setStripeCustomerId("joiafsdifjaweo");
        user.setUsername(username);

        return user;
    }

    public static String createPasswordResetUrl(HttpServletRequest request, long userId, String token) {
        var sb = new StringBuilder();
        sb.append(request.getScheme()).append("://").append(request.getServerName()).append(":")
                .append(request.getServerPort()).append(request.getContextPath())
                .append(PasswordResetController.URL_UPDATE_PASSWORD).append("?id=").append(userId).append("&token=")
                .append(token);
        return sb.toString();
    }

    public static <T extends BasicAccount> User fromPayloadToDomainUser(T payload) {
        User user = new User();
        user.setUsername(payload.getUsername());
        user.setEmail(payload.getEmail());
        user.setPassword(payload.getPassword());
        user.setFirstName(payload.getFirstName());
        user.setLastName(payload.getLastName());
        user.setDescription(payload.getDescription());
        user.setPhoneNumber(payload.getPhoneNumber());
        user.setCountry(payload.getCountry());
        user.setEnabled(true);
        return user;
    }
}
