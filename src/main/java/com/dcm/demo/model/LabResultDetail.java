package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "chi_tiet_ket_qua")
@Entity
@Data
public class LabResultDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ket_qua")
    private LabResult labResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_so")
    private Param param;

    @Column(name = "ten_chi_so")
    private String name;

    @Column(name = "gia_tri_thuc_te")
    private String value;

    @Column(name = "don_vi")
    private String unit;

    @Column(name = "khoang_tham_chieu")
    private String range;

    @Enumerated(EnumType.STRING)
    @Column(name = "danh_gia_nguong")
    private RangeStatus rangeStatus = RangeStatus.CHUA_XAC_DINH;


    public enum RangeStatus{
        CAO,
        THAP,
        TRUNG_BINH,
        CHUA_XAC_DINH
    }
}
