package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "phong_kham")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phong_kham")
    private Integer roomId;

    @Column(name = "ten_phong_kham", nullable = false)
    private String roomName;

    @Column(name = "so_phong", nullable = false, length = 10)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khoa")
    private Department department;
    @Override
    public String toString() {
        return "";
    }
}
