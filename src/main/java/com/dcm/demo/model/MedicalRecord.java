package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "phieu_kham_benh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phieu_kham")
    private Integer id;

    @Column(name = "ma_phieu_kham", unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_benh_nhan")
    private Patient patient;

    @OneToMany(mappedBy = "medicalRecord")
    private List<LabOrder>  labOrders;

    @ManyToOne
    @JoinColumn(name = "id_goi_kham")
    private HealthPlan healthPlan;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_dat_lich")
//    private Appointment appointment;

    @ManyToOne()
    @JoinColumn(name = "id_bac_si", nullable = false)
    private Doctor doctor;

    @CreationTimestamp
    @Column(name = "ngay_kham")
    private LocalDateTime date;

    @Column(name = "trieu_chung_chinh", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "kham_lam_sang", columnDefinition = "TEXT")
    private String clinicalExamination;

    @Column(name = "chan_doan", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "ke_hoach_dieu_tri", columnDefinition = "TEXT")
    private String treatmentPlan;

    @Column(name = "ghi_chu_bac_si", columnDefinition = "TEXT")
    private String note;

    @Column(name = "phi_kham", precision = 15, scale = 2)
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(name = "tong_chi_phi", precision = 15, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private RecordStatus status = RecordStatus.DANG_KHAM;

    public enum RecordStatus {
        DANG_KHAM, CHO_XET_NGHIEM, HOAN_THANH, HUY
    }
    @Override
    public String toString() {
        return "";
    }

}
