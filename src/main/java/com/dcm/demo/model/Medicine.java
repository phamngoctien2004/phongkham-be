package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thuoc")
    private Integer medicineId;

    @Column(name = "ten_thuoc", nullable = false)
    private String medicineName;

    @Column(name = "ham_luong", length = 100)
    private String concentration;

    @Column(name = "dang_bao_che", length = 100)
    private String dosageForm;
}
