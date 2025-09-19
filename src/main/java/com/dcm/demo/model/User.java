package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nguoi_dung")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguoi_dung")
    private Integer id;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "mat_khau")
    private String password;

    @Column(name = "sdt")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private Role role;

    @Column(name = "trang_thai")
    @Builder.Default
    private Boolean status = true;

    @CreationTimestamp
    @Column(name = "ngay_tao")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> relationships;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Doctor doctor;


    public enum Gender {
        NAM, NU
    }

    public enum Role {
        BENH_NHAN, BAC_SI, LE_TAN, ADMIN
    }

    @Override
    public String toString() {
        return "";
    }
}
