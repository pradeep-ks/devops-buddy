package in.devopsbuddy.web.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.persistence.repository.PlanRepository;
import in.devopsbuddy.persistence.repository.RoleRepository;
import in.devopsbuddy.persistence.repository.UserRepository;
import in.devopsbuddy.web.service.UserService;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    public PlanRepository planRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public User save(User user, PlanEnum planEnum, Set<UserRole> userRoles) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        var plan = new Plan(planEnum);
        if (!this.planRepository.existsById(planEnum.getId())) {
            plan = this.planRepository.save(plan);
        }
        user.setPlan(plan);

        // userRoles.forEach(userRole -> roleRepository.save(userRole.getRole()));
        for (UserRole userRole : userRoles) {
            this.roleRepository.saveAndFlush(userRole.getRole());
        }
        
        user.getUserRoles().addAll(userRoles);
        
        user = this.userRepository.save(user);
        return user;
    }

    @Override
    public void updatePassword(long userId, String password) {
        password = this.passwordEncoder.encode(password);
        this.userRepository.updatePassword(userId, password);
        LOGGER.debug("Password updated successfully for user {}", userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }
    
}
