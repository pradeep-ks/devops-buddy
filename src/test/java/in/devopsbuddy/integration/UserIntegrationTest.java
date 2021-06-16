package in.devopsbuddy.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.domain.Role;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserIntegrationTest extends AbstractIntegrationTest {

    @BeforeEach
    public void init() {
        assertNotNull(planRepository);
        assertNotNull(roleRepository);
        assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() throws Exception {
        Plan plan = createPlan(PlanEnum.BASIC);
        planRepository.save(plan);
        Plan retrieved = planRepository.findById(PlanEnum.BASIC.getId()).get();
        assertNotNull(retrieved);
    }

    @Test
    public void testCreateNewRole() throws Exception {
        Role role = createRole(RoleEnum.BASIC);
        roleRepository.save(role);
        Role retrieved = roleRepository.findById(RoleEnum.BASIC.getId()).get();
        assertNotNull(retrieved);
    }

    @Test
    public void testCreateNewUser(TestInfo testInfo) throws Exception {
        var username = testInfo.getDisplayName();
        var email = testInfo.getDisplayName() + "@devopsbuddy.in";
        var basicUser = createUser(username, email);

        var newUser = userRepository.findById(basicUser.getId()).get();
        assertNotNull(newUser);
        assertTrue(newUser.getId() != 0L);
        assertNotNull(newUser.getPlan());
        assertNotNull(newUser.getPlan().getId());
        var newUserRoles = newUser.getUserRoles();
        newUserRoles.forEach(item -> {
            assertNotNull(item.getRole());
            assertNotNull(item.getRole().getId());
        });
    }

    @Test
    public void testDeleteUser(TestInfo testInfo) throws Exception {
        var username = testInfo.getDisplayName();
        var email = testInfo.getDisplayName() + "@devopsbuddy.in";
        var basicUser = createUser(username, email);
        this.userRepository.deleteById(basicUser.getId());
    }

}
