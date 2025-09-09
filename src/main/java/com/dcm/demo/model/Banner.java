package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "banner")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_banner")
    private Integer bannerId;

    @Column(name = "tieu_de", nullable = false)
    private String title;

    @Column(name = "hinh_anh", nullable = false)
    private String image;

    @Column(name = "lien_ket")
    private String link;

    @Column(name = "ngay_bat_dau")
    private LocalDate startDate;

    @Column(name = "ngay_ket_thuc")
    private LocalDate endDate;

    @Column(name = "trang_thai", length = 50)
    private String status;
}
