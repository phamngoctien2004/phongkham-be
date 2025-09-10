package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ket_qua_xet_nghiem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ket_qua")
    private Integer resultId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_dinh", nullable = false, unique = true)
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si_thuc_hien")
    private Doctor performingDoctor;

    @Column(name = "ngay_thuc_hien")
    private LocalDateTime performedDate;

    @Column(name = "ket_qua_chi_tiet", columnDefinition = "TEXT")
    private String resultDetails;

    @Column(name = "file_ket_qua", length = 500)
    private String resultFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private ResultStatus status = ResultStatus.HOAN_THANH;

    public enum ResultStatus {
        HOAN_THANH, CAN_KIEM_TRA_LAI
    }
    @Override
    public String toString() {
        return "";
    }
}
