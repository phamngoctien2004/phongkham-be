package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "loai_bang_cap")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Degree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bang_cap")
    private Integer degreeId;

    @Column(name = "ten_bang_cap", nullable = false)
    private String degreeName;

    @Column(name = "phi_kham", nullable = false, precision = 12, scale = 0)
    private BigDecimal examinationFee;

    @Column(name = "ma_chuc_danh")
    private String code;
    @Override
    public String toString() {
        return "";
    }
}
