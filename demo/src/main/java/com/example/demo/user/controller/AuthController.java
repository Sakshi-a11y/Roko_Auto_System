package com.example.demo.user.controller;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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

        try {
            userService.registerUser(user, vehicle);
            return ResponseEntity.ok("Registration Successful");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, String>> login(
            @RequestParam String vehicleNumber,
            @RequestParam String password) {

        Optional<UserEntity> user = userService.authenticate(vehicleNumber, password);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid vehicle number or password"));
        }

        UserEntity loggedInUser = user.get();
        Map<String, String> response = new HashMap<>();
        response.put("fullName", loggedInUser.getName());
        response.put("contact", loggedInUser.getContact());
        response.put("address", loggedInUser.getAddress());
        response.put("vehicleNumber", vehicleNumber.toUpperCase());

        return ResponseEntity.ok(response);
    }
}