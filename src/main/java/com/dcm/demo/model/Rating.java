package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "danh_gia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_gia")
    private Integer ratingIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung")
    private User user;

    @Column(name = "diem_danh_gia")
    private Integer ratingScore; // 1-5 stars

    @Column(name = "nhan_xet", columnDefinition = "TEXT")
    private String comment;
    @Override
    public String toString() {
        return "";
    }
}
