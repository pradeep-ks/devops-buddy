package in.devopsbuddy.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.web.service.PasswordResetTokenService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordResetTokenServiceIntegrationTest extends AbstractServiceIntegrationTest {
    
    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Test
    public void testGeneratePasswordResetTokenForEmail(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        var resetToken = passwordResetTokenService.generatePasswordResetTokenForEmail(user.getEmail());
        assertNotNull(resetToken);
        assertNotNull(resetToken.getToken());
    }

    @Test
    public void testFindByToken(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        var resetToken = passwordResetTokenService.generatePasswordResetTokenForEmail(user.getEmail());
        assertNotNull(resetToken);
        assertNotNull(resetToken.getToken());

        var token = passwordResetTokenService.findByToken(resetToken.getToken());
        assertNotNull(token);
    }
}
