package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "loai_bang_cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Degree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bang_cap")
    private Integer degreeId;

    @Column(name = "ten_bang_cap", nullable = false)
    private String degreeName;

    @Column(name = "phi_kham", nullable = false, precision = 12, scale = 0)
    private BigDecimal examinationFee;
}
