package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.User;
import com.Backend.MobileApp.model.Vehicle;
import com.Backend.MobileApp.repository.VehicleRepository;
import com.Backend.MobileApp.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        User user = SecurityUtils.getCurrentUser();

        vehicleRepository.findByPlateNumber(vehicle.getPlateNumber())
                .ifPresent(v -> {
                    throw new RuntimeException("Vehicle with this plate already exists");
                });

        vehicle.setUser(user);
        vehicle.setEntryTime(LocalDateTime.now());

        return vehicleRepository.save(vehicle);
    }

    @Override
    public List<Vehicle> getMyVehicles() {
        User user = SecurityUtils.getCurrentUser();
        return vehicleRepository.findByUser(user);
    }

    @Override
    public Vehicle updateExitTime(Long vehicleId) {
        User user = SecurityUtils.getCurrentUser();

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (!vehicle.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access to this vehicle");
        }

        vehicle.setExitTime(LocalDateTime.now());
        return vehicleRepository.save(vehicle);
    }

    @Override
    public Map<String, Long> getEntriesPerDay() {
        List<Object[]> results = vehicleRepository.countEntriesPerDay(); // global, no user filter

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : results) {
            data.put(row[0].toString(), (Long) row[1]);
        }
        return data;
    }

    @Override
    public Map<String, Long> getBrandDistribution() {
        List<Object[]> results = vehicleRepository.countByBrand(); // global, no user filter

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : results) {
            data.put((String) row[0], (Long) row[1]);
        }
        return data;
    }

    @Override
    public long getTodayEntries() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);

        return vehicleRepository.countByEntryTimeBetween(start, end);
    }

    @Override
    public long getVehiclesInside() {
        return vehicleRepository.countByExitTimeIsNull();
    }

    @Override
    public Map<String, Long> getEntriesPerDayForMonth(int month, int year) {
        List<Object[]> results = vehicleRepository.countEntriesPerDay(month, year);

        Map<String, Long> data = new LinkedHashMap<>();
        for (Object[] row : results) {
            data.put(row[0].toString(), (Long) row[1]); // date as string -> count
        }
        return data;
    }

}
