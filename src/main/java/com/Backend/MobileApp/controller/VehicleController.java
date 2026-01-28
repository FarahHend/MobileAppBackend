package com.Backend.MobileApp.controller;

import com.Backend.MobileApp.model.Vehicle;
import com.Backend.MobileApp.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // Add vehicle
    @PostMapping
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.addVehicle(vehicle));
    }

    // Get vehicles of current user
    @GetMapping
    public ResponseEntity<List<Vehicle>> getMyVehicles() {
        return ResponseEntity.ok(vehicleService.getMyVehicles());
    }

    // Set exit time
    @PutMapping("/{id}/exit")
    public ResponseEntity<Vehicle> setExitTime(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.updateExitTime(id));
    }

    /** Vehicle brand distribution (global) */
    @GetMapping("/admin/brands")
    public Map<String, Long> brandDistribution() {
        return vehicleService.getBrandDistribution();
    }

    /** Vehicles entered today (global) */
    @GetMapping("/admin/today")
    public long todayEntries() {
        return vehicleService.getTodayEntries();
    }

    /** Vehicles currently inside (global) */
    @GetMapping("/admin/inside")
    public long vehiclesInside() {
        return vehicleService.getVehiclesInside();
    }

    @GetMapping("/admin/entries-per-day")
    public Map<String, Long> entriesPerDay(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return vehicleService.getEntriesPerDayForMonth(month, year);
    }

}
