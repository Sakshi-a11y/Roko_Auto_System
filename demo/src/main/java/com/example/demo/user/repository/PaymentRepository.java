package com.example.demo.user.repository;

import com.example.demo.user.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByRazorpayOrderId(String razorpayOrderId);

    Optional<PaymentEntity> findByRazorpayPaymentId(String razorpayPaymentId);

    List<PaymentEntity> findAllByOrderByPaymentDateDesc();

    List<PaymentEntity> findByVehicleVehicleId(Long vehicleId);
}
