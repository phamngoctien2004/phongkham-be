package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "benh_nhan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_benh_nhan")
    private Integer patientId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private User user;

    @Column(name = "ma_benh_nhan", unique = true, length = 20)
    private String patientCode;

    @Column(name = "nhom_mau", length = 10)
    private String bloodType;

    @Column(name = "chieu_cao", precision = 5, scale = 2)
    private BigDecimal height; // in cm

    @Column(name = "can_nang", precision = 5, scale = 2)
    private BigDecimal weight; // in kg

    @CreationTimestamp
    @Column(name = "ngay_dang_ky")
    private LocalDateTime registrationDate;
}
