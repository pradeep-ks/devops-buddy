package in.devopsbuddy.integration;

import java.util.HashSet;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.service.UserService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public abstract class AbstractServiceIntegrationTest {

    @Autowired
    protected UserService userService;

    protected User createUser(TestInfo testInfo) {
        var username = testInfo.getDisplayName();
        var email = testInfo.getDisplayName() + "@devopsbuddy.in";
        var basicUser = UserUtil.createBasicUser(username, email);
        var userRoles = new HashSet<UserRole>();
        userRoles.add(new UserRole(basicUser, new Role(RoleEnum.BASIC)));

        var newUser = userService.save(basicUser, PlanEnum.BASIC, userRoles);
        return newUser;
    }
}
