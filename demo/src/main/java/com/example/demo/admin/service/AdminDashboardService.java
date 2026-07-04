package com.example.demo.admin.service;

import com.example.demo.user.entity.PaymentEntity;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.repository.PaymentRepository;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.repository.VehicleRepository;
import com.example.demo.user.service.ViolationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminDashboardService {

    private final VehicleRepository vehicleRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ViolationService violationService;

    public AdminDashboardService(
            VehicleRepository vehicleRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            ViolationService violationService) {
        this.vehicleRepository = vehicleRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.violationService = violationService;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllRegisteredVehicles() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (VehicleEntity vehicle : vehicleRepository.findAll()) {
            UserEntity owner = vehicle.getUser();
            int strikeCount = violationService.getStrikeCount(vehicle);

            Map<String, Object> row = new HashMap<>();
            row.put("vehicleId", vehicle.getVehicleId());
            row.put("vehicleNumber", vehicle.getVehicleNumber());
            row.put("vehicleType", vehicle.getVehicletype());
            row.put("registrationDate", vehicle.getRegistrationDate() != null ? vehicle.getRegistrationDate().toString() : null);
            row.put("userId", owner.getUserId());
            row.put("ownerName", owner.getName());
            row.put("contact", owner.getContact());
            row.put("strikeCount", strikeCount);
            row.put("status", violationService.deriveStatus(strikeCount));
            result.add(row);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllPayments() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (PaymentEntity payment : paymentRepository.findAllByOrderByPaymentDateDesc()) {
            VehicleEntity vehicle = payment.getVehicle();
            UserEntity owner = vehicle.getUser();

            Map<String, Object> row = new HashMap<>();
            row.put("paymentId", payment.getPaymentId());
            row.put("vehicleId", vehicle.getVehicleId());
            row.put("vehicleNumber", vehicle.getVehicleNumber());
            row.put("ownerName", owner.getName());
            row.put("amount", payment.getAmount());
            row.put("status", payment.getStatus());
            row.put("paymentDate", payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null);
            row.put("razorpayOrderId", payment.getRazorpayOrderId());
            row.put("razorpayPaymentId", payment.getRazorpayPaymentId());
            result.add(row);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getUserDetails(Long userId) {
        return userRepository.findById(userId).map(user -> {
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("name", user.getName());
            response.put("contact", user.getContact());
            response.put("address", user.getAddress());

            List<Map<String, Object>> vehicles = new ArrayList<>();
            List<Map<String, Object>> payments = new ArrayList<>();

            for (VehicleEntity vehicle : vehicleRepository.findByUserUserId(userId)) {
                int strikeCount = violationService.getStrikeCount(vehicle);

                Map<String, Object> vehicleData = new HashMap<>();
                vehicleData.put("vehicleId", vehicle.getVehicleId());
                vehicleData.put("vehicleNumber", vehicle.getVehicleNumber());
                vehicleData.put("vehicleType", vehicle.getVehicletype());
                vehicleData.put("registrationDate",
                        vehicle.getRegistrationDate() != null ? vehicle.getRegistrationDate().toString() : null);
                vehicleData.put("strikeCount", strikeCount);
                vehicleData.put("status", violationService.deriveStatus(strikeCount));
                vehicles.add(vehicleData);

                for (PaymentEntity payment : paymentRepository.findByVehicleVehicleId(vehicle.getVehicleId())) {
                    Map<String, Object> paymentData = new HashMap<>();
                    paymentData.put("paymentId", payment.getPaymentId());
                    paymentData.put("vehicleNumber", vehicle.getVehicleNumber());
                    paymentData.put("amount", payment.getAmount());
                    paymentData.put("status", payment.getStatus());
                    paymentData.put("paymentDate",
                            payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null);
                    paymentData.put("razorpayOrderId", payment.getRazorpayOrderId());
                    paymentData.put("razorpayPaymentId", payment.getRazorpayPaymentId());
                    payments.add(paymentData);
                }
            }

            response.put("vehicles", vehicles);
            response.put("payments", payments);
            return response;
        });
    }

    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getVehicleDetails(Long vehicleId) {
        return vehicleRepository.findById(vehicleId).map(vehicle -> {
            UserEntity owner = vehicle.getUser();
            int strikeCount = violationService.getStrikeCount(vehicle);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", owner.getUserId());
            response.put("name", owner.getName());
            response.put("contact", owner.getContact());
            response.put("address", owner.getAddress());

            Map<String, Object> vehicleData = new HashMap<>();
            vehicleData.put("vehicleId", vehicle.getVehicleId());
            vehicleData.put("vehicleNumber", vehicle.getVehicleNumber());
            vehicleData.put("vehicleType", vehicle.getVehicletype());
            vehicleData.put("registrationDate",
                    vehicle.getRegistrationDate() != null ? vehicle.getRegistrationDate().toString() : null);
            vehicleData.put("strikeCount", strikeCount);
            vehicleData.put("status", violationService.deriveStatus(strikeCount));
            response.put("vehicle", vehicleData);

            List<Map<String, Object>> payments = new ArrayList<>();
            for (PaymentEntity payment : paymentRepository.findByVehicleVehicleId(vehicle.getVehicleId())) {
                Map<String, Object> paymentData = new HashMap<>();
                paymentData.put("paymentId", payment.getPaymentId());
                paymentData.put("vehicleNumber", vehicle.getVehicleNumber());
                paymentData.put("amount", payment.getAmount());
                paymentData.put("status", payment.getStatus());
                paymentData.put("paymentDate",
                        payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : null);
                paymentData.put("razorpayOrderId", payment.getRazorpayOrderId());
                paymentData.put("razorpayPaymentId", payment.getRazorpayPaymentId());
                payments.add(paymentData);
            }

            response.put("payments", payments);
            return response;
        });
    }
}
