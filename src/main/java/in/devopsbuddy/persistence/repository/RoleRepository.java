package in.devopsbuddy.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.devopsbuddy.persistence.domain.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
}
