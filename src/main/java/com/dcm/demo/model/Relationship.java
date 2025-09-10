package com.dcm.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lien_ket_benh_nhan")
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
}
