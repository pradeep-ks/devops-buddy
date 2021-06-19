package in.devopsbuddy.web.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.persistence.domain.Plan;
import in.devopsbuddy.persistence.repository.PlanRepository;
import in.devopsbuddy.web.service.PlanService;

@Service
@Transactional(readOnly = true)
public class PlanServiceImpl implements PlanService {

    @Autowired
    public PlanRepository planRepository;

    @Override
    public Optional<Plan> findById(int id) {
        return this.planRepository.findById(id);
    }

    @Override
    @Transactional
    public Plan save(int planId) {
        Plan plan = null;
        if (planId == 1) {
            plan = this.planRepository.save(new Plan(PlanEnum.BASIC));
        } else if (planId == 2) {
            plan = this.planRepository.save(new Plan(PlanEnum.PRO));
        } else {
            throw new IllegalArgumentException("Plan id " + planId + " not recognized!");
        }
        return plan;
    }
    
}
