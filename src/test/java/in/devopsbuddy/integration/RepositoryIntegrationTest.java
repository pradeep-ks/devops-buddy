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
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.persistence.repository.PlanRepository;
import in.devopsbuddy.persistence.repository.RoleRepository;
import in.devopsbuddy.persistence.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class RepositoryIntegrationTest {

    private static final int BASIC_PLAN_ID = 1;
    private static final int USER_ROLE_ID = 1;
    
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
        Plan plan = createBasicPlan();
        planRepository.save(plan);
        Plan retrieved = planRepository.findById(BASIC_PLAN_ID).get();
        assertNotNull(retrieved);
    }

    @Test
    public void testCreateNewRole() throws Exception {
        Role role = createUserRole();
        roleRepository.save(role);
        Role retrieved = roleRepository.findById(USER_ROLE_ID).get();
        assertNotNull(retrieved);
    }

    @Test
    public void testCreateNewUser() throws Exception {
        var plan = createBasicPlan();
        planRepository.save(plan);

        var user = createUser();
        user.setPlan(plan);

        var role = createUserRole();
        var userRoles = new HashSet<UserRole>();
        var userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
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

    private Role createUserRole() {
        Role role = new Role();
        role.setId(USER_ROLE_ID);
        role.setName("ROLE_USER");
        return role;
    }

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("Basic");
        return plan;
    }

    private User createUser() {
        User user = new User();
        user.setCountry("India");
        user.setDescription("A dummy user for testing integration");
        user.setEmail("dummy.user@gmail.com");
        user.setEnabled(true);
        user.setFirstName("Dummy");
        user.setLastName("User");
        user.setPassword("{noop}password");
        user.setPhoneNumber("7982176473");
        user.setProfileImageUrl("https://www.google.co.in?q=dummy");
        user.setStripeCustomerId("joiafsdifjaweo");

        return user;
    }
}
