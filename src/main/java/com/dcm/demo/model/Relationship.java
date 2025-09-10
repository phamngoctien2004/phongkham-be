package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "lien_ket_benh_nhan")
@Data
public class Relationship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_benh_nhan")
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "id_nguoi_dung")
    private User user;

    @Column(name = "vai_tro")
    private String relational;
    @Override
    public String toString() {
        return "";
    }
}
