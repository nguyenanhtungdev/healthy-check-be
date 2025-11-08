package org.tung.healthycheck.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.tung.healthycheck.model.CalorieAggregate;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.services.AuthService;
import org.tung.healthycheck.services.SummaryService;

import java.time.LocalDate;
import java.time.YearMonth;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    private SummaryService summaryService;
    @Autowired private AuthService authService;

    // Tổng hợp tuần
    @GetMapping("/week")
    public ResponseEntity<CalorieAggregate> week(@RequestParam(required=false) LocalDate anyDay) {
        User user = authService.getCurrentUser();
        LocalDate d = anyDay != null ? anyDay : LocalDate.now();
        return ResponseEntity.ok(summaryService.computeAndSaveWeekly(user.getId(), d));
    }

    // Tổng hợp tháng
    @GetMapping("/month")
    public ResponseEntity<CalorieAggregate> month(@RequestParam(required=false) String ym) {
        User user = authService.getCurrentUser();
        YearMonth yearMonth = (ym != null) ? YearMonth.parse(ym) : YearMonth.now();
        return ResponseEntity.ok(summaryService.computeAndSaveMonthly(user.getId(), yearMonth));
    }
}
