package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(name = "lich_lam_viec_bac_si")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich")
    private Integer scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "thu_trong_tuan", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "khung_gio_bat_dau", nullable = false)
    private LocalTime startTime;

    @Column(name = "khung_gio_ket_thuc", nullable = false)
    private LocalTime endTime;

    public enum DayOfWeek {
        T2, T3, T4, T5, T6, T7, CN
    }
}
