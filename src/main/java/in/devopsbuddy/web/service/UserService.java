package in.devopsbuddy.web.service;

import java.util.Set;

import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.domain.UserRole;

public interface UserService {
    
    User save(User user, PlanEnum planEnum, Set<UserRole> userRoles);
    
    void updatePassword(long userId, String password);
    
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
}
