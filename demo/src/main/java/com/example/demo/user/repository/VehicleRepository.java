package com.example.demo.user.repository;

import com.example.demo.user.entity.VehicleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<VehicleEntity, Long> {

    Optional<VehicleEntity> findByVehicleNumber(String vehicleNumber);

    List<VehicleEntity> findByUserUserId(Long userId);
}