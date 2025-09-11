package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "dich_vu_kham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dich_vu")
    private Integer id;

    @Column(name = "ma_dich_vu", unique = true, length = 50)
    private String code;

    @Column(name = "ten_dich_vu", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai")
    private ServiceType type = ServiceType.DICH_VU;

    @Column(name = "gia", precision = 15, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    @Column(name = "trang_thai")
    private Boolean status = true;

    @OneToOne
    @JoinColumn(name = "id_phong")
    private Room room;

//    danh sach chi tiet cua goi kham
    @OneToMany(mappedBy = "service")
    private List<healthPlanDetail> healthPlanDetails;

//    danh sach xet nghiep thuoc cac goi kham nao
    @OneToMany(mappedBy = "serviceDetail")
    private List<healthPlanDetail> belongHealthPlan;
    public enum ServiceType {
        DICH_VU, XET_NGHIEM, CHUAN_DOAN_HINH_ANH, KHAC, CHUYEN_KHOA
    }
    @Override
    public String toString() {
        return "";
    }
}
