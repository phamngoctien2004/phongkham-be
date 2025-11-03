package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "khoa_bac_si")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khoa")
    private Integer id;

    @Column(name = "ten_khoa", nullable = false)
    private String name;

    @Column(name = "sdt", nullable = false, length = 15)
    private String phone;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    @Column(name = "trang_thai")
    private Boolean status = true;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Doctor> doctors;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Room> rooms;
    @Override
    public String toString() {
        return "";
    }
}
