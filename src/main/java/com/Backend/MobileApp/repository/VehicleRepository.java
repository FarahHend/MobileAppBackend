package com.Backend.MobileApp.repository;

import com.Backend.MobileApp.model.Vehicle;
import com.Backend.MobileApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPlateNumber(String plateNumber);

    List<Vehicle> findByUser(User user);

    @Query("""
        SELECT DATE(v.entryTime), COUNT(v)
        FROM Vehicle v
        WHERE v.user = :user
        GROUP BY DATE(v.entryTime)
        ORDER BY DATE(v.entryTime)
    """)
    List<Object[]> countEntriesPerDay();

    @Query("""
        SELECT v.brand, COUNT(v)
        FROM Vehicle v
        WHERE v.user = :user AND v.brand IS NOT NULL
        GROUP BY v.brand
    """)
    List<Object[]> countByBrand(User user);

    long countByUserAndEntryTimeBetween(
            User user,
            LocalDateTime start,
            LocalDateTime end
    );

    long countByUserAndExitTimeIsNull(User user);



    @Query("SELECT v.brand, COUNT(v) FROM Vehicle v GROUP BY v.brand")
    List<Object[]> countByBrand();

    long countByEntryTimeBetween(LocalDateTime start, LocalDateTime end);

    long countByExitTimeIsNull();

    // Count vehicles grouped by entry date for a given month and year
    @Query("SELECT FUNCTION('DATE', v.entryTime), COUNT(v) " +
            "FROM Vehicle v " +
            "WHERE FUNCTION('MONTH', v.entryTime) = :month AND FUNCTION('YEAR', v.entryTime) = :year " +
            "GROUP BY FUNCTION('DATE', v.entryTime) " +
            "ORDER BY FUNCTION('DATE', v.entryTime)")
    List<Object[]> countEntriesPerDay(@Param("month") int month,
                                      @Param("year") int year);

}
