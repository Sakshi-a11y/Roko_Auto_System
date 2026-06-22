package com.example.demo.user.controller;

import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    // ➤ Add vehicle for user
    @PostMapping("/{userId}")
    public VehicleEntity addVehicle(@PathVariable Long userId,
            @RequestBody VehicleEntity vehicle) {
        return vehicleService.addVehicle(userId, vehicle);
    }

    // ➤ Get all vehicles of a user
    @GetMapping("/user/{userId}")
    public List<VehicleEntity> getVehiclesByUser(@PathVariable Long userId) {
        return vehicleService.getVehiclesByUser(userId);
    }

    // ➤ Get vehicle by ID
    @GetMapping("/{vehicleId}")
    public VehicleEntity getVehicleById(@PathVariable Long vehicleId) {
        return vehicleService.getVehicleById(vehicleId);
    }

    // ➤ Delete vehicle
    @DeleteMapping("/{vehicleId}")
    public String deleteVehicle(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return "Vehicle deleted successfully";
    }
}