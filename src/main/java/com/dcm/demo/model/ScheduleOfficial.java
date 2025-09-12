package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "lich_lam_chinh_thuc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleOfficial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si", nullable = false)
    private Doctor doctor;

    @Column(name = "ngay_lam")
    private LocalDate date;

    @Column(name = "khung_gio_bat_dau", nullable = false)
    private LocalTime startTime;

    @Column(name = "khung_gio_ket_thuc", nullable = false)
    private LocalTime endTime;

    @Column(name = "so_luong")
    private int max = 2;

    @Column(name = "da_xac_nhan")
    private int booked = 0;

    @Column(name = "trang_thai")
    private boolean status = true;
}
