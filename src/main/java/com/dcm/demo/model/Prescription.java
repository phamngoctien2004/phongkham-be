package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "don_thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_don_thuoc")
    private Integer prescriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_kham", nullable = false)
    private MedicalRecord medicalRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bac_si")
    private Doctor doctor;

    @Column(name = "ma_don_thuoc", unique = true, length = 20)
    private String prescriptionCode;

    @CreationTimestamp
    @Column(name = "ngay_ke_don")
    private LocalDateTime prescriptionDate;

    @Column(name = "huong_dan_chung", columnDefinition = "TEXT")
    private String generalInstructions;
}
