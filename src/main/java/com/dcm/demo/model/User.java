package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nguoi_dung")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguoi_dung")
    private Integer id;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "mat_khau")
    private String password;

    @Column(name = "ho_ten", nullable = false)
    private String fullName;

    @Column(name = "sdt", length = 20)
    private String phone;

    @Column(name = "dia_chi", columnDefinition = "TEXT")
    private String address;

    @Column(name = "CCCD", columnDefinition = "TEXT")
    private String cccd;

    @Column(name = "ngay_sinh")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false)
    private Role role;

    @Column(name = "anh_nguoi_dung", length = 500)
    private String profileImage;

    @Column(name = "trang_thai")
    private Boolean status = true;

    @CreationTimestamp
    @Column(name = "ngay_tao")
    private LocalDateTime createdAt;

    public enum Gender {
        NAM, NU
    }

    public enum Role {
        BENH_NHAN, BAC_SI, LE_TAN, ADMIN
    }
}
