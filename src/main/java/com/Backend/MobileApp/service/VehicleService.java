package com.Backend.MobileApp.service;

import com.Backend.MobileApp.model.Vehicle;

import java.util.List;
import java.util.Map;

public interface VehicleService {

    Vehicle addVehicle(Vehicle vehicle);

    List<Vehicle> getMyVehicles();

    Vehicle updateExitTime(Long vehicleId);

    Map<String, Long> getEntriesPerDay();

    Map<String, Long> getBrandDistribution();

    long getTodayEntries();

    long getVehiclesInside();

    Map<String, Long> getEntriesPerDayForMonth(int month, int year);
}
