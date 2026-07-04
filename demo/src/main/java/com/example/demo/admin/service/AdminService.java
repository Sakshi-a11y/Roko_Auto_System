package com.example.demo.admin.service;

import com.example.demo.admin.entity.AdminEntity;
import com.example.demo.admin.repository.AdminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public void registerAdmin(String name, String email, String password) {
        String normalizedEmail = email.trim().toLowerCase();

        if (adminRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("An admin account with this email already exists. Please login.");
        }

        AdminEntity admin = new AdminEntity();
        admin.setName(name.trim());
        admin.setEmail(normalizedEmail);
        admin.setPassword(password);
        adminRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public Optional<AdminEntity> authenticate(String email, String password) {
        return adminRepository.findByEmailIgnoreCase(email.trim())
                .filter(admin -> admin.getPassword().equals(password));
    }
}
