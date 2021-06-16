package in.devopsbuddy.integration;

import java.util.Arrays;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.persistence.repository.PlanRepository;
import in.devopsbuddy.persistence.repository.RoleRepository;
import in.devopsbuddy.persistence.repository.UserRepository;
import in.devopsbuddy.util.UserUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public abstract class AbstractIntegrationTest {
    @Autowired
    protected PlanRepository planRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    protected Role createRole(RoleEnum roleEnum) {
        return new Role(roleEnum);
    }

    protected Plan createPlan(PlanEnum planEnum) {
        return new Plan(planEnum);
    }

    protected User createUser(String username, String email) {
        var basicPlan = createPlan(PlanEnum.BASIC);
        this.planRepository.save(basicPlan);

        var basicUser = UserUtil.createBasicUser(username, email);

        var basicRole = createRole(RoleEnum.BASIC);
        this.roleRepository.save(basicRole);

        basicUser.setPlan(basicPlan);
        basicUser.getUserRoles().addAll(Arrays.asList(new UserRole(basicUser, basicRole)));

        basicUser = this.userRepository.save(basicUser);
        return basicUser;
    }

    protected User createUser(TestInfo testInfo) {
        return createUser(testInfo.getDisplayName(), testInfo.getDisplayName() + "@devopsbuddy.in");
    }
}
