package com.Backend.MobileApp.repository;

import com.Backend.MobileApp.model.Attendance;
import com.Backend.MobileApp.model.AttendanceStatus;
import com.Backend.MobileApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndDate(User user, LocalDate date);

    List<Attendance> findByUser(User user);

    List<Attendance> findByDate(LocalDate date);

    @Query("""
        SELECT a.status, COUNT(a)
        FROM Attendance a
        WHERE a.user = :user
        GROUP BY a.status
    """)
    List<Object[]> countByStatus(User user);

    @Query("""
        SELECT a.date, COUNT(a)
        FROM Attendance a
        WHERE a.user = :user AND a.status = 'PRESENT'
        GROUP BY a.date
        ORDER BY a.date
    """)
    List<Object[]> countPresentPerDay(User user);

    long countByUserAndStatus(User user, AttendanceStatus status);

    List<Attendance> findByDateBetween(LocalDate start, LocalDate end);

    List<Attendance> findByUserAndDateBetween(
            User user,
            LocalDate start,
            LocalDate end
    );

    @Query("""
        SELECT a.status, COUNT(a)
        FROM Attendance a
        WHERE a.user = :user
        AND MONTH(a.date) = :month
        AND YEAR(a.date) = :year
        GROUP BY a.status
    """)
    List<Object[]> countMonthlyStatus(
            @Param("user") User user,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("""
    SELECT a FROM Attendance a
    WHERE MONTH(a.date) = :month AND YEAR(a.date) = :year
""")
    List<Attendance> findByMonthAndYear(int month, int year);

    long countByStatus(AttendanceStatus status);

    @Query("""
        SELECT a FROM Attendance a
        WHERE a.date BETWEEN :start AND :end
    """)
    List<Attendance> findAllInPeriod(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

}
