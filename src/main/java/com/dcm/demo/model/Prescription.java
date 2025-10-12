package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "don_thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_don_thuoc")
    private Integer id;

    @Column(name = "ma_don_thuoc", unique = true, length = 20)
    private String code;

    @OneToOne
    @JoinColumn(name = "id_phieu_kham", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "bac_si_ke_thuoc")
    private String doctorCreated;

    @CreationTimestamp
    @Column(name = "ngay_ke_don")
    private LocalDateTime prescriptionDate;

    @Column(name = "huong_dan_chung", columnDefinition = "TEXT")
    private String generalInstructions;

    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescriptionDetail> prescriptionDetails = new ArrayList<>();
    @Override
    public String toString() {
        return "";
    }
}
