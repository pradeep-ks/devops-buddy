package in.devopsbuddy.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.devopsbuddy.persistence.domain.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {
    
}
