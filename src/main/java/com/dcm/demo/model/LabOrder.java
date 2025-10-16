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
    private Integer id;

    @Column(name = "ma_chi_dinh")
    private String code;
    @ManyToOne
    @JoinColumn(name = "id_phieu_kham")
    private MedicalRecord medicalRecord;

    @ManyToOne
    @JoinColumn(name = "id_dich_vu")
    private HealthPlan healthPlan;

    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "id_bac_si_chi_dinh")
    private Doctor orderingDoctor;


    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "id_bac_si_thuc_hien")
    private Doctor performingDoctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private TestStatus status = TestStatus.CHO_THUC_HIEN;

    @Column(name = "gia", precision = 15, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "ngay_chi_dinh")
    private LocalDateTime orderDate;

    @Column(name = "chuan_doan")
    private String diagnosis;
    @Column(name = "ngay_hen_tra_ket_qua")
    private LocalDateTime expectedResultDate;

    @CreationTimestamp
    @Column(name = "thoi_gian_vao_hang")
    private LocalDateTime queueTime;

    @Column(name = "phong_thuc_hien")
    private String room;
    public enum TestStatus {
        CHO_THUC_HIEN, DANG_THUC_HIEN, HOAN_THANH, HUY_BO
    }
    @OneToOne(mappedBy = "labOrder")
    private LabResult labResult;
    @Override
    public String toString() {
        return "";
    }
}
