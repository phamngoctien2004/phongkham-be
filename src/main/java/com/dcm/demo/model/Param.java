package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chi_so_do")
@Data
public class Param {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_chi_so")
    private String name;

    @Column(name = "don_vi")
    private String unit;

    @Column(name = "khoang_tham_chieu")
    private String range;

    @Column(name = "phuong_thuc")
    private String method;

    @OneToMany(mappedBy = "param", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HealthPlanParam> healthPlanParams = new ArrayList<>();
}
