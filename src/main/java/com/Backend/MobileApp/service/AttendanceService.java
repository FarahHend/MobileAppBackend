package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    Attendance checkIn();

    Attendance checkOut();

    Attendance getTodayAttendance();

    List<Attendance> getMyAttendanceHistory();

    List<Attendance> getAttendanceByUser(Long userId);

    List<Attendance> getAttendanceByDate(LocalDate date);
}
