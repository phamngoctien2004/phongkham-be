package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_dinh_xet_nghiem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chi_dinh")
    private Integer orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_kham", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dich_vu")
    private ExaminationService examinationService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si_chi_dinh", nullable = false)
    private Doctor orderingDoctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TestStatus status = TestStatus.CHO_THUC_HIEN;

    @Column(name = "gia", precision = 15, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "ngay_chi_dinh")
    private LocalDateTime orderDate;

    @Column(name = "ngay_hen_tra_ket_qua")
    private LocalDateTime expectedResultDate;

    @CreationTimestamp
    @Column(name = "thoi_gian_vao_hang")
    private LocalDateTime queueTime;

    public enum TestStatus {
        CHO_THUC_HIEN, HOAN_THANH, HUY_BO
    }
}
