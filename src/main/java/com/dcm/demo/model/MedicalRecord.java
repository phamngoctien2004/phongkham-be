package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "phieu_kham_benh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phieu_kham")
    private Integer recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_benh_nhan")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dat_lich")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si", nullable = false)
    private Doctor doctor;

    @Column(name = "ma_phieu_kham", unique = true, length = 20)
    private String recordCode;

    @CreationTimestamp
    @Column(name = "ngay_kham")
    private LocalDateTime examinationDate;

    @Column(name = "trieu_chung_chinh", columnDefinition = "TEXT")
    private String mainSymptoms;

    @Column(name = "kham_lam_sang", columnDefinition = "TEXT")
    private String clinicalExamination;

    @Column(name = "chan_doan", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "ke_hoach_dieu_tri", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "ghi_chu_bac_si", columnDefinition = "TEXT")
    private String doctorNotes;

    @Column(name = "phi_kham", precision = 15, scale = 2)
    private BigDecimal examinationFee = BigDecimal.ZERO;

    @Column(name = "tong_chi_phi", precision = 15, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private RecordStatus status = RecordStatus.DANG_KHAM;

    public enum RecordStatus {
        DANG_KHAM, CHO_XET_NGHIEM, HOAN_THANH, HUY
    }
}
