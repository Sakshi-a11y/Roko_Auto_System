package com.example.demo.user.controller;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.entity.ViolationLogEntity;
import com.example.demo.user.repository.ViolationLogRepository;
import com.example.demo.user.service.UserService;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final ViolationLogRepository violationLogRepository;

    public AuthController(UserService userService, ViolationLogRepository violationLogRepository) {
        this.userService = userService;
        this.violationLogRepository = violationLogRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login.html";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login.html";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "redirect:/register.html";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {

        UserEntity user = new UserEntity();
        user.setName(request.get("name"));
        user.setContact(request.get("contact"));
        user.setAddress(request.get("address"));
        user.setPassword(request.get("password"));

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleNumber(request.get("vehicleNumber"));
        vehicle.setVehicletype(request.get("vehicleType"));
        vehicle.setRegistrationDate(LocalDate.now());

        try {
            userService.registerUser(user, vehicle);
            return ResponseEntity.ok("Registration Successful");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed due to a database constraint. Please try again.");
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String vehicleNumber,
            @RequestParam String password) {

        Optional<UserEntity> user = userService.authenticate(vehicleNumber, password);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid vehicle number or password"));
        }

        UserEntity loggedInUser = user.get();
        Map<String, Object> response = new HashMap<>();
        response.put("fullName", loggedInUser.getName());
        response.put("contact", loggedInUser.getContact());
        response.put("address", loggedInUser.getAddress());
        response.put("vehicleNumber", vehicleNumber.toUpperCase());

        int strikeCount = 0;
        String status = "active";
        if (loggedInUser.getVehicles() != null && !loggedInUser.getVehicles().isEmpty()) {
            VehicleEntity vehicle = loggedInUser.getVehicles().get(0);
            response.put("vehicletype", vehicle.getVehicletype() != null ? vehicle.getVehicletype() : "N/A");
            response.put("registrationDate",
                    vehicle.getRegistrationDate() != null ? vehicle.getRegistrationDate().toString() : "N/A");

            Optional<ViolationLogEntity> latestLog = violationLogRepository
                    .findTopByVehicleOrderByViolationDateDesc(vehicle);
            if (latestLog.isPresent() && latestLog.get().getStrikeCount() != null) {
                strikeCount = latestLog.get().getStrikeCount();
                status = strikeCount >= 3 ? "blocked" : "active";
            }
        } else {
            response.put("vehicletype", "N/A");
            response.put("registrationDate", "N/A");
        }

        response.put("strikeCount", strikeCount);
        response.put("status", status);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/violation/increase")
    @ResponseBody
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
                    log.setViolationDate(java.time.LocalDateTime.now());
                    return violationLogRepository.save(log);
                });

        latestLog.setStrikeCount(latestLog.getStrikeCount() == null ? 1 : latestLog.getStrikeCount() + 1);
        latestLog.setViolationDate(java.time.LocalDateTime.now());
        violationLogRepository.save(latestLog);

        Map<String, Object> response = new HashMap<>();
        response.put("strikeCount", latestLog.getStrikeCount());
        response.put("status", latestLog.getStrikeCount() >= 3 ? "blocked" : "active");
        return ResponseEntity.ok(response);
    }
}