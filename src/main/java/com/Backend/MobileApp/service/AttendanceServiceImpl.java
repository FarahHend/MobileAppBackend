package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.*;
import com.Backend.MobileApp.repository.AttendanceRepository;
import com.Backend.MobileApp.repository.UserRepository;
import com.Backend.MobileApp.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.TreeMap;


@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    private static final LocalTime LATE_LIMIT = LocalTime.of(8, 0);
    private static final LocalTime EARLY_LIMIT = LocalTime.of(18, 0);

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository,
                                 UserRepository userRepository) {
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Attendance checkIn() {
        User user = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByUserAndDate(user, today)
                .orElse(new Attendance(user, today, AttendanceStatus.PRESENT));

        if (attendance.getCheckIn() != null) {
            throw new RuntimeException("Already checked in");
        }

        attendance.setCheckIn(LocalDateTime.now());
        attendance.setStatus(AttendanceStatus.PRESENT);

        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance checkOut() {
        User user = SecurityUtils.getCurrentUser();
        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository
                .findByUserAndDate(user, today)
                .orElseThrow(() -> new RuntimeException("No check-in found"));

        if (attendance.getCheckOut() != null) {
            throw new RuntimeException("Already checked out");
        }

        attendance.setCheckOut(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    @Override
    public Attendance getTodayAttendance() {
        User user = SecurityUtils.getCurrentUser();
        return attendanceRepository
                .findByUserAndDate(user, LocalDate.now())
                .orElse(null);
    }

    @Override
    public List<Attendance> getMyAttendanceHistory() {
        User user = SecurityUtils.getCurrentUser();
        return attendanceRepository.findByUser(user);
    }

    @Override
    public List<Attendance> getAttendanceByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return attendanceRepository.findByUser(user);
    }

    @Override
    public List<Attendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    @Override
    public Map<String, Long> getStatusDistribution() {
        User user = SecurityUtils.getCurrentUser();

        List<Object[]> results = attendanceRepository.countByStatus(user);
        Map<String, Long> data = new LinkedHashMap<>();

        for (Object[] row : results) {
            data.put(row[0].toString(), (Long) row[1]);
        }
        return data;
    }

    @Override
    public Map<String, Long> getPresentPerDay() {
        User user = SecurityUtils.getCurrentUser();

        List<Object[]> results = attendanceRepository.countPresentPerDay(user);
        Map<String, Long> data = new LinkedHashMap<>();

        for (Object[] row : results) {
            data.put(row[0].toString(), (Long) row[1]);
        }
        return data;
    }

    @Override
    public String getTodayStatus() {
        User user = SecurityUtils.getCurrentUser();

        return attendanceRepository
                .findByUserAndDate(user, LocalDate.now())
                .map(attendance -> attendance.getStatus().name())
                .orElse("NOT_MARKED");
    }


    @Override
    public long getPresentDays() {
        User user = SecurityUtils.getCurrentUser();
        return attendanceRepository.countByUserAndStatus(user, AttendanceStatus.PRESENT);
    }

    @Override
    public long getAbsentDays() {
        User user = SecurityUtils.getCurrentUser();
        return attendanceRepository.countByUserAndStatus(user, AttendanceStatus.ABSENT);
    }

    @Override
    public Map<String, Long> getMonthlyStats(int month, int year) {
        User user = SecurityUtils.getCurrentUser();

        Map<String, Long> result = new HashMap<>();
        List<Object[]> rows =
                attendanceRepository.countMonthlyStatus(user, month, year);

        for (Object[] row : rows) {
            result.put(row[0].toString(), (Long) row[1]);
        }

        return result;
    }

    @Override
    public double getAverageWorkingHours(int month, int year) {
        User user = SecurityUtils.getCurrentUser();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findByUserAndDateBetween(user, start, end);

        return attendances.stream()
                .filter(a -> a.getCheckIn() != null && a.getCheckOut() != null)
                .mapToLong(a ->
                        Duration.between(a.getCheckIn(), a.getCheckOut()).toMinutes()
                )
                .average()
                .orElse(0) / 60.0; // hours
    }

    @Override
    public long getLateCheckins(int month, int year) {
        User user = SecurityUtils.getCurrentUser();
        LocalTime lateLimit = LocalTime.of(9, 0);

        return attendanceRepository
                .findByUserAndDateBetween(
                        user,
                        LocalDate.of(year, month, 1),
                        LocalDate.of(year, month,
                                LocalDate.of(year, month, 1).lengthOfMonth())
                )
                .stream()
                .filter(a ->
                        a.getCheckIn() != null &&
                                a.getCheckIn().toLocalTime().isAfter(lateLimit)
                )
                .count();
    }

    @Override
    public long getEarlyCheckouts(int month, int year) {
        User user = SecurityUtils.getCurrentUser();
        LocalTime earlyLimit = LocalTime.of(17, 0);

        return attendanceRepository
                .findByUserAndDateBetween(
                        user,
                        LocalDate.of(year, month, 1),
                        LocalDate.of(year, month,
                                LocalDate.of(year, month, 1).lengthOfMonth())
                )
                .stream()
                .filter(a ->
                        a.getCheckOut() != null &&
                                a.getCheckOut().toLocalTime().isBefore(earlyLimit)
                )
                .count();
    }

    // ----------------------------------------------------
    // PIE CHART: PRESENT / ABSENT / LEAVE (ALL USERS)
    // ----------------------------------------------------
    @Override
    public Map<String, Long> getGlobalStatusDistribution() {
        return attendanceRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus().name(),
                        Collectors.counting()
                ));
    }

    // ----------------------------------------------------
    // BAR CHART: PRESENT EMPLOYEES PER DAY
    // ----------------------------------------------------
    @Override
    public Map<String, Long> getGlobalPresentPerDay(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findByDateBetween(start, end)
                .stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .collect(Collectors.groupingBy(
                        a -> a.getDate().toString(),
                        TreeMap::new,
                        Collectors.counting()
                ));
    }

    // ----------------------------------------------------
    // KPI: TOTAL PRESENT DAYS (ALL EMPLOYEES)
    // ----------------------------------------------------
    @Override
    public long getGlobalPresentDays() {
        return attendanceRepository.countByStatus(AttendanceStatus.PRESENT);
    }

    // ----------------------------------------------------
    // KPI: TOTAL ABSENT DAYS
    // ----------------------------------------------------
    @Override
    public long getGlobalAbsentDays() {
        return attendanceRepository.countByStatus(AttendanceStatus.ABSENT);
    }

    // ----------------------------------------------------
    // MONTHLY SUMMARY (PRESENT / ABSENT / LEAVE)
    // ----------------------------------------------------
    @Override
    public Map<String, Long> getGlobalMonthlyStats(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findAllInPeriod(start, end)
                .stream()
                .collect(Collectors.groupingBy(
                        a -> a.getStatus().name(),
                        Collectors.counting()
                ));
    }

    // ----------------------------------------------------
    // AVG WORKING HOURS (ALL EMPLOYEES)
    // ----------------------------------------------------
    @Override
    public double getGlobalAverageWorkingHours(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendances =
                attendanceRepository.findAllInPeriod(start, end);

        return attendances.stream()
                .filter(a -> a.getCheckIn() != null && a.getCheckOut() != null)
                .mapToDouble(a -> Duration
                        .between(a.getCheckIn(), a.getCheckOut())
                        .toMinutes() / 60.0
                )
                .average()
                .orElse(0.0);
    }

    // ----------------------------------------------------
    // LATE CHECK-INS (AFTER 09:00)
    // ----------------------------------------------------
    @Override
    public long getGlobalLateCheckins(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findAllInPeriod(start, end)
                .stream()
                .filter(a -> a.getCheckIn() != null)
                .filter(a -> a.getCheckIn().toLocalTime().isAfter(LATE_LIMIT))
                .count();
    }

    // ----------------------------------------------------
    // EARLY CHECK-OUTS (BEFORE 17:00)
    // ----------------------------------------------------
    @Override
    public long getGlobalEarlyCheckouts(int month, int year) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return attendanceRepository.findAllInPeriod(start, end)
                .stream()
                .filter(a -> a.getCheckOut() != null)
                .filter(a -> a.getCheckOut().toLocalTime().isBefore(EARLY_LIMIT))
                .count();
    }
}
