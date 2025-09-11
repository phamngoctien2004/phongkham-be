package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chi_tiet_dich_vu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class healthPlanDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_goi_kham")
    private HealthPlan service;

    @ManyToOne
    @JoinColumn(name = "id_xet_nghiem")
    private HealthPlan serviceDetail;
}
