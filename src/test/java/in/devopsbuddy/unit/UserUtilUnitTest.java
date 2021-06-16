package in.devopsbuddy.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.controller.PasswordResetController;

public class UserUtilUnitTest {
    
    private MockHttpServletRequest mockHttpServletRequest;

    @BeforeEach
    public void init() {
        this.mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    public void testPasswordResetEmailUrlConstruction() throws Exception {
        mockHttpServletRequest.setServerPort(8080);
        var userId = 1234L;
        var token = UUID.randomUUID().toString();
        var expectedUrl = String.format("http://localhost:8080%s?id=%d&token=%s", PasswordResetController.PASSWORD_RESET_URL, userId, token);
        var actualUrl = UserUtil.createPasswordResetUrl(mockHttpServletRequest, userId, token);
        assertEquals(expectedUrl, actualUrl);
    }
}
