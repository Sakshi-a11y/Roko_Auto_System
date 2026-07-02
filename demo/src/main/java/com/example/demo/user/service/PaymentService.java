package com.example.demo.user.service;

import com.example.demo.user.entity.PaymentEntity;
import com.example.demo.user.entity.VehicleEntity;
import com.example.demo.user.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ViolationService violationService;
    private final String keyId;
    private final String keySecret;
    private final int fineAmountInRupees;

    public PaymentService(
            PaymentRepository paymentRepository,
            ViolationService violationService,
            @Value("${razorpay.key-id}") String keyId,
            @Value("${razorpay.key-secret}") String keySecret,
            @Value("${razorpay.fine.amount}") int fineAmountInRupees) {
        this.paymentRepository = paymentRepository;
        this.violationService = violationService;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.fineAmountInRupees = fineAmountInRupees;
    }

    public Map<String, Object> getPaymentConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("keyId", keyId);
        config.put("amount", fineAmountInRupees * 100);
        config.put("currency", "INR");
        return config;
    }

    @Transactional
    public Map<String, Object> createOrder(VehicleEntity vehicle) throws RazorpayException {
        int strikeCount = violationService.getStrikeCount(vehicle);
        if (strikeCount < 3) {
            throw new IllegalArgumentException("Payment is required only after 3 strikes.");
        }

        int amountInPaise = fineAmountInRupees * 100;
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "fine_" + vehicle.getVehicleNumber() + "_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", 1);

        Order order = client.orders.create(orderRequest);

        PaymentEntity payment = new PaymentEntity();
        payment.setVehicle(vehicle);
        payment.setRazorpayOrderId(order.get("id"));
        payment.setAmount(amountInPaise);
        payment.setCurrency("INR");
        payment.setStatus("CREATED");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Map<String, Object> response = new HashMap<>();
        response.put("keyId", keyId);
        response.put("orderId", order.get("id"));
        response.put("amount", amountInPaise);
        response.put("currency", "INR");
        return response;
    }

    @Transactional
    public Map<String, Object> verifyAndResetStrikes(
            VehicleEntity vehicle,
            String orderId,
            String paymentId,
            String signature) throws RazorpayException {

        Optional<PaymentEntity> existingPayment = paymentRepository.findByRazorpayPaymentId(paymentId);
        if (existingPayment.isPresent() && "PAID".equals(existingPayment.get().getStatus())) {
            Map<String, Object> response = violationService.getViolationDetails(vehicle);
            response.put("message", "Payment already verified. Vehicle is active.");
            return response;
        }

        PaymentEntity payment = paymentRepository.findByRazorpayOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment order not found."));

        if (!payment.getVehicle().getVehicleId().equals(vehicle.getVehicleId())) {
            throw new IllegalArgumentException("Payment order does not belong to this vehicle.");
        }

        JSONObject attributes = new JSONObject();
        attributes.put("razorpay_order_id", orderId);
        attributes.put("razorpay_payment_id", paymentId);
        attributes.put("razorpay_signature", signature);

        if (!Utils.verifyPaymentSignature(attributes, keySecret)) {
            throw new IllegalArgumentException("Payment verification failed.");
        }

        payment.setRazorpayPaymentId(paymentId);
        payment.setStatus("PAID");
        payment.setPaidAt(LocalDateTime.now());
        paymentRepository.save(payment);

        Map<String, Object> response = violationService.resetStrikes(vehicle);
        response.put("message", "Payment successful. Vehicle reactivated.");
        return response;
    }
}
