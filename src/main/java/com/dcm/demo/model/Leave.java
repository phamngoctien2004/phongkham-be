package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "nghi_phep")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nghi_phep")
    private Integer leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si")
    private Doctor doctor;

    @Column(name = "batdau_nghi")
    private LocalDate startDate;

    @Column(name = "ketthuc_nghi")
    private LocalDate endDate;

    @Column(name = "ly_do_nghi")
    private String reason;

    @Column(name = "trang_thai_nghi")
    private Integer leaveStatus; // 0: Pending, 1: Approved, 2: Rejected

    @Column(name = "ngay_gui")
    private LocalDate submitDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_duyet")
    private User approver;
}
