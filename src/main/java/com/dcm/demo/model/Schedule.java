package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "lich_lam_viec_bac_si")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si", nullable = false)
    private Doctor doctor;

    @Column(name = "id_khoa")
    private Integer departmentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "thu_trong_tuan", nullable = false)
    private DayOfWeek day;

    @Enumerated(EnumType.STRING)
    @Column(name = "ca_lam_viec", nullable = false)
    private Schedule.Shift shift;
    @Column(name = "khung_gio_bat_dau", nullable = false)
    private LocalTime startTime;

    @Column(name = "khung_gio_ket_thuc", nullable = false)
    private LocalTime endTime;

    public enum Shift {
        SANG, CHIEU, TOI
    }
}
