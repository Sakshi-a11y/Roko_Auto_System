package com.example.demo.user.service;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.VehicleRepository;
import org.springframework.stereotype.Service;

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
    public void registerUser(UserEntity user, VehicleEntity vehicle) {

        UserEntity savedUser = userRepository.save(user);

        vehicle.setUser(savedUser);

        vehicleRepository.save(vehicle);
    }

    // ➤ Login using vehicle number + password
    public boolean login(String vehicleNumber, String password) {

        VehicleEntity vehicle = vehicleRepository
                .findByVehicleNumber(vehicleNumber)
                .orElse(null);

        if (vehicle == null) {
            return false;
        }

        UserEntity user = vehicle.getUser();

        return user.getPassword().equals(password);
    }
}