package in.devopsbuddy.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.devopsbuddy.persistence.domain.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
}
