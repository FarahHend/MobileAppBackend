package com.Backend.MobileApp.controller;

import com.Backend.MobileApp.model.Attendance;
import com.Backend.MobileApp.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    // ✅ Employee APIs
    @PostMapping("/check-in")
    public Attendance checkIn() {
        return attendanceService.checkIn();
    }

    @PostMapping("/check-out")
    public Attendance checkOut() {
        return attendanceService.checkOut();
    }

    @GetMapping("/today")
    public Attendance today() {
        return attendanceService.getTodayAttendance();
    }

    @GetMapping("/me")
    public List<Attendance> myHistory() {
        return attendanceService.getMyAttendanceHistory();
    }

    // ✅ Admin / Manager APIs
    @GetMapping("/user/{userId}")
    public List<Attendance> byUser(@PathVariable Long userId) {
        return attendanceService.getAttendanceByUser(userId);
    }

    @GetMapping("/date/{date}")
    public List<Attendance> byDate(@PathVariable LocalDate date) {
        return attendanceService.getAttendanceByDate(date);
    }

    //by user
    @GetMapping("/status")
    public Map<String, Long> statusDistribution() {
        return attendanceService.getStatusDistribution();
    }

    //by user

    @GetMapping("/per-day")
    public Map<String, Long> presentPerDay() {
        return attendanceService.getPresentPerDay();
    }

    @GetMapping("/admin/today")
    public String todayStatus() {
        return attendanceService.getTodayStatus();
    }

    //by user
    @GetMapping("/present-days")
    public long presentDays() {
        return attendanceService.getPresentDays();
    }

    //by user
    @GetMapping("/absent-days")
    public long absentDays() {
        return attendanceService.getAbsentDays();
    }

    //by user
    @GetMapping("/monthly")
    public Map<String, Long> monthly(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getMonthlyStats(month, year);
    }

    //bu user
    @GetMapping("/working-hours")
    public double workingHours(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getAverageWorkingHours(month, year);
    }

    //by user

    @GetMapping("/late-checkin")
    public long lateCheckin(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getLateCheckins(month, year);
    }

    //by user
    @GetMapping("/early-checkout")
    public long earlyCheckout(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getEarlyCheckouts(month, year);
    }

    /** Global status distribution (PRESENT / ABSENT / LEAVE) */
    @GetMapping("/admin/status")
    public Map<String, Long> globalStatusDistribution() {
        return attendanceService.getGlobalStatusDistribution();
    }

    /** Global present per day for a given month and year for ADMIN */
    @GetMapping("/admin/per-day")
    public Map<String, Long> globalPresentPerDay(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getGlobalPresentPerDay(month, year);
    }

    /** Total global present days for ADMIN */
    @GetMapping("/admin/present-days")
    public long globalPresentDays() {
        return attendanceService.getGlobalPresentDays();
    }

    /** Total global absent days for ADMIN*/
    @GetMapping("/admin/absent-days")
    public long globalAbsentDays() {
        return attendanceService.getGlobalAbsentDays();
    }

    /** Monthly stats: count per day for given month and year for ADMIN */
    @GetMapping("/admin/monthly")
    public Map<String, Long> globalMonthlyStats(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getGlobalMonthlyStats(month, year);
    }

    /** Average working hours for given month and year for ADMIN */
    @GetMapping("/admin/working-hours")
    public double globalAverageWorkingHours(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getGlobalAverageWorkingHours(month, year);
    }

    /** Total late check-ins for given month and year for ADMIN */
    @GetMapping("/admin/late-checkin")
    public long globalLateCheckins(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getGlobalLateCheckins(month, year);
    }

    /** Total early check-outs for given month and year for ADMIN */
    @GetMapping("/admin/early-checkout")
    public long globalEarlyCheckouts(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return attendanceService.getGlobalEarlyCheckouts(month, year);
    }
}
