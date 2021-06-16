package in.devopsbuddy.util;

import in.devopsbuddy.persistence.domain.User;

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
}
