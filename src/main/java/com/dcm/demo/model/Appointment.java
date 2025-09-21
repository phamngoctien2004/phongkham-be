package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "dat_lich_kham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dat_lich")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dich_vu_kham")
    private HealthPlan healthPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khoa")
    private Department department;

    @Column(name = "ho_ten", nullable = false)
    private String fullName;

    @Column(name = "sdt", nullable = false, length = 15)
    private String phone;

    @Column(name = "dia_chi", nullable = false)
    private String address;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "ngay_sinh")
    private LocalDate birth;

    @Column(name = "ngay_kham", nullable = false)
    private LocalDate date;

    @Column(name = "gio_kham", nullable = false)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private AppointmentStatus status = AppointmentStatus.CHO_XAC_NHAN;

    @Column(name = "trieu_chung", columnDefinition = "TEXT")
    private String symptoms;

    @CreationTimestamp
    @Column(name = "ngay_dat_lich")
    private LocalDateTime bookingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_xac_nhan")
    private User confirmedBy;

    @ManyToOne
    @JoinColumn(name = "id_benh_nhan")
    private Patient patient;

    public enum AppointmentStatus {
        CHO_XAC_NHAN, DA_XAC_NHAN, KHONG_DEN, DA_DEN
    }
    @Override
    public String toString() {
        return "";
    }
}
