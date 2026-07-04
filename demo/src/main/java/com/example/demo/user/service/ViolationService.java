package com.example.demo.user.service;

import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.entity.ViolationLogEntity;
import com.example.demo.user.repository.ViolationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.Optional;

@Service
public class ViolationService {

    private final ViolationLogRepository violationLogRepository;

    public ViolationService(ViolationLogRepository violationLogRepository) {
        this.violationLogRepository = violationLogRepository;
    }

    @Transactional
    public Map<String, Object> increaseStrike(VehicleEntity vehicle) {
        int newCount = getStrikeCount(vehicle) + 1;

        ViolationLogEntity log = new ViolationLogEntity();
        log.setVehicle(vehicle);
        log.setStrikeCount(newCount);
        log.setViolationDate(LocalDateTime.now());
        violationLogRepository.save(log);

        return buildStrikeResponse(newCount);
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
        ViolationLogEntity log = new ViolationLogEntity();
        log.setVehicle(vehicle);
        log.setStrikeCount(0);
        log.setViolationDate(LocalDateTime.now());
        violationLogRepository.save(log);
        return buildStrikeResponse(0);
    }

    public String deriveStatus(int strikeCount) {
        return strikeCount >= 3 ? "blocked" : "active";
    }

    private Map<String, Object> buildStrikeResponse(int strikeCount) {
        Map<String, Object> response = new HashMap<>();
        response.put("strikeCount", strikeCount);
        response.put("status", deriveStatus(strikeCount));
        return response;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getViolationHistory(VehicleEntity vehicle) {
        List<Map<String, Object>> history = new ArrayList<>();

        for (ViolationLogEntity log : violationLogRepository.findByVehicleOrderByViolationDateDesc(vehicle)) {
            if (log.getStrikeCount() == null || log.getStrikeCount() <= 0) {
                continue;
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("strikeCount", log.getStrikeCount());
            entry.put("violationDate",
                    log.getViolationDate() != null ? log.getViolationDate().toString() : null);
            history.add(entry);
        }

        return history;
    }

    @Transactional(readOnly = true)
    public List<ViolationLogEntity> getStrikeLogsOldestFirst(VehicleEntity vehicle) {
        return violationLogRepository.findByVehicleOrderByViolationDateDesc(vehicle).stream()
                .filter(log -> log.getStrikeCount() != null && log.getStrikeCount() > 0)
                .sorted(Comparator.comparing(
                        ViolationLogEntity::getViolationDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ViolationLogEntity> getLatestBlockViolation(VehicleEntity vehicle) {
        return violationLogRepository.findByVehicleOrderByViolationDateDesc(vehicle).stream()
                .filter(log -> log.getStrikeCount() != null && log.getStrikeCount() >= 3)
                .findFirst();
    }
}
