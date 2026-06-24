package com.example.demo.user.service;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public UserService(UserRepository userRepository,
                       VehicleRepository vehicleRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // ➤ Register User + Vehicle
    @Transactional
    public void registerUser(UserEntity user, VehicleEntity vehicle) {
        if (vehicleRepository.findByVehicleNumber(vehicle.getVehicleNumber()).isPresent()) {
            throw new IllegalArgumentException(
                    "This vehicle number is already registered. Please login instead.");
        }

        vehicle.setUser(user);
        user.setVehicles(java.util.List.of(vehicle));
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> authenticate(String vehicleNumber, String password) {
        return vehicleRepository.findByVehicleNumber(vehicleNumber)
                .filter(vehicle -> vehicle.getUser().getPassword().equals(password))
                .map(VehicleEntity::getUser);
    }

    // ➤ Login using vehicle number + password
    public boolean login(String vehicleNumber, String password) {
        return authenticate(vehicleNumber, password).isPresent();
    }
}