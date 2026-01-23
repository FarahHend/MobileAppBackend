package com.Backend.MobileApp.controller;

import com.Backend.MobileApp.model.Attendance;
import com.Backend.MobileApp.service.AttendanceService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
}
