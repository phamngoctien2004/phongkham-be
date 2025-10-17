package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "anh_xet_nghiem")
@Entity
@Data
public class ImageLab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_ket_qua")
    private LabResult labResult;

    @Column(name = "url")
    private String url;
}
