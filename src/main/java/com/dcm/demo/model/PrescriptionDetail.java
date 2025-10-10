package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chi_tiet_don_thuoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chi_tiet")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_don_thuoc", nullable = false)
    private Prescription prescription;

    @ManyToOne
    @JoinColumn(name = "id_thuoc")
    private Medicine medicine;

    @Column(name = "so_luong")
    private Integer quantity;

    @Column(name = "huong_dan_su_dung", columnDefinition = "TEXT")
    private String usageInstructions;
    @Override
    public String toString() {
        return "";
    }
}
