package org.tung.healthycheck.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.dto.CalorieTargetDTO;
import org.tung.healthycheck.model.CalorieTarget;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.CalorieTargetRepository;

import java.time.LocalDate;

@Service
public class TargetService {
    @Autowired
    private CalorieTargetRepository targetRepository;

    @Transactional
    public void upsertTargets(User user, CalorieTargetDTO dto) {
        CalorieTarget t = new CalorieTarget();
        t.setUser(user);
        t.setDailyTarget(dto.getDailyTarget());
        t.setWeeklyTarget(dto.getWeeklyTarget());
        t.setMonthlyTarget(dto.getMonthlyTarget());
        t.setCreatedAt(LocalDate.now());
        targetRepository.save(t);
    }
}
