package com.example.demo.user.controller;

import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.entity.ViolationLogEntity;
import com.example.demo.user.repository.ViolationLogRepository;
import com.example.demo.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/violation")
public class ViolationController {

    private final UserService userService;
    private final ViolationLogRepository violationLogRepository;

    public ViolationController(UserService userService, ViolationLogRepository violationLogRepository) {
        this.userService = userService;
        this.violationLogRepository = violationLogRepository;
    }

    @GetMapping("/increase")
    @PostMapping("/increase")
    public ResponseEntity<Map<String, Object>> increaseStrike(@RequestParam String vehicleNumber) {
        Optional<VehicleEntity> vehicleOpt = userService.findVehicleByNumber(vehicleNumber);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehicle not found"));
        }

        VehicleEntity vehicle = vehicleOpt.get();
        ViolationLogEntity latestLog = violationLogRepository.findTopByVehicleOrderByViolationDateDesc(vehicle)
                .orElseGet(() -> {
                    ViolationLogEntity log = new ViolationLogEntity();
                    log.setVehicle(vehicle);
                    log.setStrikeCount(0);
                    log.setViolationDate(LocalDateTime.now());
                    return violationLogRepository.save(log);
                });

        latestLog.setStrikeCount(latestLog.getStrikeCount() == null ? 1 : latestLog.getStrikeCount() + 1);
        latestLog.setViolationDate(LocalDateTime.now());
        violationLogRepository.save(latestLog);

        Map<String, Object> response = new HashMap<>();
        response.put("strikeCount", latestLog.getStrikeCount());
        response.put("status", latestLog.getStrikeCount() >= 3 ? "blocked" : "active");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getViolationDetails(@RequestParam String vehicleNumber) {
        Optional<VehicleEntity> vehicleOpt = userService.findVehicleByNumber(vehicleNumber);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehicle not found"));
        }

        VehicleEntity vehicle = vehicleOpt.get();
        Optional<ViolationLogEntity> latestLog = violationLogRepository.findTopByVehicleOrderByViolationDateDesc(vehicle);
        int strikeCount = latestLog.map(ViolationLogEntity::getStrikeCount).orElse(0);
        String status = strikeCount >= 3 ? "blocked" : "active";

        Map<String, Object> response = new HashMap<>();
        response.put("vehicleNumber", vehicle.getVehicleNumber());
        response.put("vehicleType", vehicle.getVehicletype());
        response.put("strikeCount", strikeCount);
        response.put("status", status);
        return ResponseEntity.ok(response);
    }
}
