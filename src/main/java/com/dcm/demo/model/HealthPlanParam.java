package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chi_tiet_chi_so_dich_vu")
@Data
public class HealthPlanParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name="id_dich_vu")
    private HealthPlan healthPlan;

    @ManyToOne
    @JoinColumn(name="id_chi_so")
    private Param param;
}
