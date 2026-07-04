package com.example.demo.user.repository;

import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.entity.ViolationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ViolationLogRepository extends JpaRepository<ViolationLogEntity, Long> {
    Optional<ViolationLogEntity> findTopByVehicleOrderByViolationDateDesc(VehicleEntity vehicle);

    List<ViolationLogEntity> findByVehicleOrderByViolationDateDesc(VehicleEntity vehicle);
}
