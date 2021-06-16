package in.devopsbuddy.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Test
    public void testCreateNewUser(TestInfo testInfo) throws Exception {
        var newUser = createUser(testInfo);
        assertNotNull(newUser);
        assertNotNull(newUser.getId());
    }
    
}
