package in.devopsbuddy.persistence.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import in.devopsbuddy.persistence.domain.Plan;

@Repository
public interface PlanRepository extends CrudRepository<Plan, Integer> {
    
}
