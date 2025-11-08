package org.tung.healthycheck.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.tung.healthycheck.model.CalorieAggregate;
import org.tung.healthycheck.model.User;
import org.tung.healthycheck.repository.CalorieAggregateRepository;
import org.tung.healthycheck.repository.CalorieTargetRepository;
import org.tung.healthycheck.repository.MealRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;

@Service
public class SummaryService {
    @Autowired
    private MealRepository mealRepository;
    @Autowired private CalorieTargetRepository targetRepository;
    @Autowired private CalorieAggregateRepository aggregateRepository;

    // Tổng hợp tuần/tháng chạy định kỳ
    @Scheduled(cron = "0 5 0 * * *") // 00:05 mỗi ngày
    public void computeAggregatesDaily() {
        // Tùy hệ thống của bạn, lấy danh sách user từ nguồn Users service
        // Ở đây minh họa: giả sử có list userIds cần tổng hợp
    }

    public CalorieAggregate computeAndSaveWeekly(UUID userId, LocalDate anyDayInWeek) {
        LocalDate start = anyDayInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end   = anyDayInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        int total = sumBetween(userId, start, end);
        int target = targetRepository.findTopByUser_IdOrderByCreatedAtDesc(userId)
                .map(t -> Optional.ofNullable(t.getWeeklyTarget()).orElse(0)).orElse(0);
        return saveAggregate(userId, "WEEK", start, end, total, target);
    }

    public CalorieAggregate computeAndSaveMonthly(UUID userId, YearMonth ym) {
        LocalDate start = ym.atDay(1);
        LocalDate end   = ym.atEndOfMonth();
        int total = sumBetween(userId, start, end);
        int target = targetRepository.findTopByUser_IdOrderByCreatedAtDesc(userId)
                .map(t -> Optional.ofNullable(t.getMonthlyTarget()).orElse(0)).orElse(0);
        return saveAggregate(userId, "MONTH", start, end, total, target);
    }

    private int sumBetween(UUID userId, LocalDate start, LocalDate end) {
        // kéo từng ngày (đơn giản, dễ hiểu). Có thể tối ưu bằng query group-by nếu muốn.
        int sum = 0;
        LocalDate d = start;
        while (!d.isAfter(end)) {
            sum += mealRepository.findByUser_IdAndDate(userId, d).stream()
                    .mapToInt(m -> Optional.ofNullable(m.getTotalCalories()).orElse(0)).sum();
            d = d.plusDays(1);
        }
        return sum;
    }

    private CalorieAggregate saveAggregate(UUID userId, String period, LocalDate start, LocalDate end, int total, int target) {
        CalorieAggregate ag = new CalorieAggregate();
        ag.setUser(new User(){ { setId(userId);} }); // chỉ set id để map FK
        ag.setPeriod(period);
        ag.setStartDate(start);
        ag.setEndDate(end);
        ag.setTotalCalories(total);
        ag.setTargetCalories(target);
        ag.setStatus(total < target ? "BELOW" : (total == target ? "ON_TRACK" : "ABOVE"));
        return aggregateRepository.save(ag);
    }
}
