package com.example.demo.user.controller;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.VehicleRepository;
import com.example.demo.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuthControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    void registerShouldPersistUserAndVehicle() {
        String vehicleNumber = "UP99ZZ9999";
        UserEntity user = new UserEntity();
        user.setName("TestUser");
        user.setContact("1234567890");
        user.setAddress("Test Address");
        user.setPassword("password123");

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleNumber(vehicleNumber);
        vehicle.setVehicletype("Car");
        vehicle.setRegistrationDate(LocalDate.now());

        userService.registerUser(user, vehicle);

        Optional<UserEntity> savedUser = userRepository.findAll().stream()
                .filter(existingUser -> "TestUser".equals(existingUser.getName()))
                .findFirst();

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getVehicles()).hasSize(1);
        assertThat(vehicleRepository.findByVehicleNumber(vehicleNumber)).isPresent();
    }
}
