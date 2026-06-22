package com.example.demo.user.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String contact;
    private String address;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VehicleEntity> vehicles;

    public UserEntity() {
    }

    public UserEntity(Long userId, String name, String contact, String address, String password,
            List<VehicleEntity> vehicles) {
        this.userId = userId;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.password = password;
        this.vehicles = vehicles;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<VehicleEntity> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<VehicleEntity> vehicles) {
        this.vehicles = vehicles;
    }

}
