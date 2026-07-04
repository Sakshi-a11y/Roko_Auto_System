package com.example.demo.admin.controller;

import com.example.demo.admin.entity.AdminEntity;
import com.example.demo.admin.service.AdminDashboardService;
import com.example.demo.admin.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final AdminDashboardService adminDashboardService;

    public AdminController(AdminService adminService, AdminDashboardService adminDashboardService) {
        this.adminService = adminService;
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping({"", "/"})
    public String adminHome() {
        return "redirect:/admin-login.html";
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "redirect:/admin-login.html";
    }

    @GetMapping("/register")
    public String adminRegisterPage() {
        return "redirect:/admin-register.html";
    }

    @GetMapping("/dashboard")
    public String adminDashboardPage() {
        return "redirect:/admin-dashboard.html";
    }

    @GetMapping("/vehicles")
    public String adminVehiclesPage() {
        return "redirect:/admin-vehicles.html";
    }

    @GetMapping("/payments")
    public String adminPaymentsPage() {
        return "redirect:/admin-payments.html";
    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> register(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            return ResponseEntity.badRequest().body("Please fill in all fields.");
        }

        if (password.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters.");
        }

        try {
            adminService.registerAdmin(name, email, password);
            return ResponseEntity.ok("Admin registration successful");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String email,
            @RequestParam String password) {

        Optional<AdminEntity> admin = adminService.authenticate(email, password);

        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }

        AdminEntity loggedInAdmin = admin.get();
        Map<String, Object> response = new HashMap<>();
        response.put("adminId", loggedInAdmin.getAdminId());
        response.put("name", loggedInAdmin.getName());
        response.put("email", loggedInAdmin.getEmail());
        response.put("role", "admin");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/vehicles")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllVehicles() {
        return ResponseEntity.ok(adminDashboardService.getAllRegisteredVehicles());
    }

    @GetMapping("/api/payments")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAllPayments() {
        return ResponseEntity.ok(adminDashboardService.getAllPayments());
    }

    @GetMapping("/api/users/{userId}")
    @ResponseBody
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        return adminDashboardService.getUserDetails(userId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")));
    }

    @GetMapping("/api/vehicles/{vehicleId}")
    @ResponseBody
    public ResponseEntity<?> getVehicleDetails(@PathVariable Long vehicleId) {
        return adminDashboardService.getVehicleDetails(vehicleId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Vehicle not found")));
    }
}
