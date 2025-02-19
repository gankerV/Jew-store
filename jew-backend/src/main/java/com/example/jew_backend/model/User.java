package com.example.jew_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING) // Lưu Enum dưới dạng String
    @Column(nullable = false)
    private Role role;

    // Enum cho role
    public enum Role {
        CUSTOMER, ADMIN, USER;

        public static Role fromString(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }
            return Role.valueOf(value.toUpperCase()); // Chuyển thành chữ HOA trước khi gán
        }
    }

    // Constructors
    public User() {}

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        if (role != null) {
            this.role = Role.valueOf(role.name().toUpperCase()); // Đảm bảo luôn là chữ HOA
        }
    }

    // Đảm bảo role luôn là chữ HOA trước khi lưu vào database
    @PrePersist
    @PreUpdate
    private void ensureRoleUpperCase() {
        if (this.role != null) {
            this.role = Role.valueOf(this.role.name().toUpperCase());
        }
    }
}
