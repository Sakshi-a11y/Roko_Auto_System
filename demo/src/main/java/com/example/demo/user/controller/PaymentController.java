package com.example.demo.user.controller;

import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.service.PaymentService;
import com.example.demo.user.service.UserService;
import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfig() {
        return ResponseEntity.ok(paymentService.getPaymentConfig());
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam String vehicleNumber) {
        Optional<VehicleEntity> vehicleOpt = userService.findVehicleByNumber(vehicleNumber);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehicle not found"));
        }

        try {
            return ResponseEntity.ok(paymentService.createOrder(vehicleOpt.get()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (RazorpayException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Unable to create payment order."));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) {
        String vehicleNumber = request.get("vehicleNumber");
        String orderId = request.get("razorpay_order_id");
        String paymentId = request.get("razorpay_payment_id");
        String signature = request.get("razorpay_signature");

        if (vehicleNumber == null || orderId == null || paymentId == null || signature == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing payment verification details."));
        }

        Optional<VehicleEntity> vehicleOpt = userService.findVehicleByNumber(vehicleNumber);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehicle not found"));
        }

        try {
            return ResponseEntity.ok(
                    paymentService.verifyAndResetStrikes(vehicleOpt.get(), orderId, paymentId, signature));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (RazorpayException ex) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Payment verification failed."));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getPaymentHistory(@RequestParam String vehicleNumber) {
        Optional<VehicleEntity> vehicleOpt = userService.findVehicleByNumber(vehicleNumber);
        if (vehicleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vehicle not found"));
        }

        return ResponseEntity.ok(paymentService.getPaymentDetailHistory(vehicleOpt.get()));
    }
}
