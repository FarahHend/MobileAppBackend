package com.Backend.MobileApp.repository;

import com.Backend.MobileApp.model.Attendance;
import com.Backend.MobileApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndDate(User user, LocalDate date);

    List<Attendance> findByUser(User user);

    List<Attendance> findByDate(LocalDate date);
}
