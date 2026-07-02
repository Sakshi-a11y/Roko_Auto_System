package com.example.demo.user.service;

import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.entity.ViolationLogEntity;
import com.example.demo.user.repository.ViolationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ViolationService {

    private final ViolationLogRepository violationLogRepository;

    public ViolationService(ViolationLogRepository violationLogRepository) {
        this.violationLogRepository = violationLogRepository;
    }

    @Transactional
    public Map<String, Object> increaseStrike(VehicleEntity vehicle) {
        ViolationLogEntity latestLog = getOrCreateLatestLog(vehicle);
        latestLog.setStrikeCount(latestLog.getStrikeCount() == null ? 1 : latestLog.getStrikeCount() + 1);
        latestLog.setViolationDate(LocalDateTime.now());
        violationLogRepository.save(latestLog);
        return buildStrikeResponse(latestLog.getStrikeCount());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getViolationDetails(VehicleEntity vehicle) {
        int strikeCount = getStrikeCount(vehicle);
        Map<String, Object> response = new HashMap<>();
        response.put("vehicleNumber", vehicle.getVehicleNumber());
        response.put("vehicleType", vehicle.getVehicletype());
        response.put("strikeCount", strikeCount);
        response.put("status", deriveStatus(strikeCount));
        return response;
    }

    @Transactional(readOnly = true)
    public int getStrikeCount(VehicleEntity vehicle) {
        return violationLogRepository.findTopByVehicleOrderByViolationDateDesc(vehicle)
                .map(ViolationLogEntity::getStrikeCount)
                .orElse(0);
    }

    @Transactional
    public Map<String, Object> resetStrikes(VehicleEntity vehicle) {
        ViolationLogEntity latestLog = getOrCreateLatestLog(vehicle);
        latestLog.setStrikeCount(0);
        latestLog.setViolationDate(LocalDateTime.now());
        violationLogRepository.save(latestLog);
        return buildStrikeResponse(0);
    }

    public String deriveStatus(int strikeCount) {
        return strikeCount >= 3 ? "blocked" : "active";
    }

    private ViolationLogEntity getOrCreateLatestLog(VehicleEntity vehicle) {
        return violationLogRepository.findTopByVehicleOrderByViolationDateDesc(vehicle)
                .orElseGet(() -> {
                    ViolationLogEntity log = new ViolationLogEntity();
                    log.setVehicle(vehicle);
                    log.setStrikeCount(0);
                    log.setViolationDate(LocalDateTime.now());
                    return violationLogRepository.save(log);
                });
    }

    private Map<String, Object> buildStrikeResponse(int strikeCount) {
        Map<String, Object> response = new HashMap<>();
        response.put("strikeCount", strikeCount);
        response.put("status", deriveStatus(strikeCount));
        return response;
    }
}
