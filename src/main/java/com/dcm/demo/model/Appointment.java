package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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

    @Column(name = "ngay_kham", nullable = false)
    private LocalDate date;

    @Column(name = "gio_kham", nullable = false)
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private AppointmentStatus status = AppointmentStatus.CHO_THANH_TOAN;

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
        DA_XAC_NHAN, KHONG_DEN, HOAN_THANH, CHO_THANH_TOAN,HUY
    }
    @Override
    public String toString() {
        return "";
    }
}
