package com.example.demo.user.controller;

import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.service.UserService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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
    public String register(@RequestBody Map<String, String> request) {

        UserEntity user = new UserEntity();
        user.setName(request.get("name"));
        user.setContact(request.get("contact"));
        user.setAddress(request.get("address"));
        user.setPassword(request.get("password"));

        VehicleEntity vehicle = new VehicleEntity();
        vehicle.setVehicleNumber(request.get("vehicleNumber"));
        vehicle.setVehicletype(request.get("vehicleType"));

        userService.registerUser(user, vehicle);

        return "Registration Successful";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String vehicleNumber,
            @RequestParam String password,
            Model model) {

        boolean isValid = userService.login(vehicleNumber, password);

        if (isValid) {
            return "dashboard";
        }

        model.addAttribute("error", "Invalid Vehicle Number or Password");
        return "login";
    }
}