package com.example.demo.user.service;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository,
            UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    // ➤ Add Vehicle to User
    public VehicleEntity addVehicle(Long userId, VehicleEntity vehicle) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 🔥 important: relation set
        vehicle.setUser(user);

        return vehicleRepository.save(vehicle);
    }

    // ➤ Get all vehicles of a user
    public List<VehicleEntity> getVehiclesByUser(Long userId) {

        // optional validation
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        return vehicleRepository.findByUserUserId(userId);
    }

    // ➤ Get single vehicle by ID
    public VehicleEntity getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));
    }

    // ➤ Delete vehicle
    public void deleteVehicle(Long vehicleId) {

        VehicleEntity vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        vehicleRepository.delete(vehicle);
    }
}