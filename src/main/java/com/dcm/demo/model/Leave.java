package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "nghi_phep")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si")
    private Doctor doctor;

    @Column(name = "ngay_nghi")
    private LocalDate date;

    @Column(name = "bat_dau")
    private LocalTime startTime;

    @Column(name = "ket_thuc")
    private LocalTime endTime;

    @Column(name = "ly_do_nghi")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai_nghi")
    private Leave.leaveStatus leaveStatus;

    @Column(name = "ngay_gui")
    private LocalDate submitDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet")
    private User approver;

    @Override
    public String toString() {
        return "";
    }

    public enum type {
        SANG, CHIEU, TOI
    }

    public enum leaveStatus {
        CHO_DUYET, DA_DUYET, TU_CHOI
    }
}
