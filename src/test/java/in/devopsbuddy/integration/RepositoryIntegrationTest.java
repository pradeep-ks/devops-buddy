package in.devopsbuddy.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.persistence.repository.PlanRepository;
import in.devopsbuddy.persistence.repository.RoleRepository;
import in.devopsbuddy.persistence.repository.UserRepository;
import in.devopsbuddy.util.UserUtil;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoryIntegrationTest {

    @Autowired
    public PlanRepository planRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRepository userRepository;

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
    public void testCreateNewUser() throws Exception {
        var plan = createPlan(PlanEnum.BASIC);
        planRepository.save(plan);

        var user = UserUtil.createBasicUser();
        user.setPlan(plan);

        var role = createRole(RoleEnum.BASIC);
        var userRoles = new HashSet<UserRole>();
        var userRole = new UserRole(user, role);
        userRoles.add(userRole);

        user.getUserRoles().addAll(userRoles);

        userRoles.forEach(ur -> roleRepository.save(ur.getRole()));

        user = userRepository.save(user);

        var newUser = userRepository.findById(user.getId()).get();
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

    private Role createRole(RoleEnum roleEnum) {
        return new Role(roleEnum);
    }

    private Plan createPlan(PlanEnum planEnum) {
        return new Plan(planEnum);
    }

}
