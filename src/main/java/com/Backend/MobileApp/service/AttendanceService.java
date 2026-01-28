package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AttendanceService {

    Attendance checkIn();

    Attendance checkOut();

    Attendance getTodayAttendance();

    List<Attendance> getMyAttendanceHistory();

    List<Attendance> getAttendanceByUser(Long userId);

    List<Attendance> getAttendanceByDate(LocalDate date);

    Map<String, Long> getStatusDistribution();

    Map<String, Long> getPresentPerDay();

    String getTodayStatus();

    long getPresentDays();

    long getAbsentDays();

    Map<String, Long> getMonthlyStats(int month, int year);

    double getAverageWorkingHours(int month, int year);

    long getLateCheckins(int month, int year);

    long getEarlyCheckouts(int month, int year);

    // ===== ADMIN (GLOBAL) =====
    Map<String, Long> getGlobalStatusDistribution();

    Map<String, Long> getGlobalPresentPerDay(int month, int year);

    long getGlobalPresentDays();
    long getGlobalAbsentDays();

    Map<String, Long> getGlobalMonthlyStats(int month, int year);

    double getGlobalAverageWorkingHours(int month, int year);

    long getGlobalLateCheckins(int month, int year);
    long getGlobalEarlyCheckouts(int month, int year);
}
