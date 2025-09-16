package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

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
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_dinh", nullable = false, unique = true)
    private LabOrder labOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si_thuc_hien")
    private Doctor performingDoctor;

    @CreationTimestamp
    @Column(name = "ngay_thuc_hien")
    private LocalDateTime date;

    @Column(name = "ket_qua_chi_tiet", columnDefinition = "TEXT")
    private String resultDetails;

    @Column(name = "giai_thich")
    private String explanation;

    @Column(name = "ghi_chu_ky_thuat")
    private String note;

    @Column(name="ket_luan")
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private Status status = Status.HOAN_THANH;

    public enum Status {
        HOAN_THANH, CAN_KIEM_TRA_LAI
    }
    @Override
    public String toString() {
        return "";
    }
}
