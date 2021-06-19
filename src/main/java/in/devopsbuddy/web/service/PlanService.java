package in.devopsbuddy.web.service;

import java.util.Optional;

import in.devopsbuddy.persistence.domain.Plan;

public interface PlanService {
    
    Optional<Plan> findById(int id);

    Plan save(int planId);
}
