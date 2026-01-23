package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.*;
import com.Backend.MobileApp.repository.AttendanceRepository;
import com.Backend.MobileApp.repository.UserRepository;
import com.Backend.MobileApp.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

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
}
