package in.devopsbuddy.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class ServiceIntegrationTest {
    
    @Autowired
    private UserService userService;

    @Test
    public void testCreateNewUser() throws Exception {
        var basicUser = UserUtil.createBasicUser();
        var userRoles = new HashSet<UserRole>();
        userRoles.add(new UserRole(basicUser, new Role(RoleEnum.BASIC)));

        var newUser = userService.save(basicUser, PlanEnum.BASIC, userRoles);
        assertNotNull(newUser);
        assertNotNull(newUser.getId());
    }
}
