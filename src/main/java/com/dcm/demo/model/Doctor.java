package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "bac_si")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bac_si")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_nguoi_dung")
    private User user;

    @Column(name = "ho_ten", nullable = false)
    private String fullName;

    @Column(name = "sdt", length = 20)
    private String phone;

    @Column(name = "dia_chi")
    private String address;

    @Column(name = "ngay_sinh")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh")
    private User.Gender gender;

    @Column(name = "anh_nguoi_dung", length = 500)
    private String profileImage;

    @ManyToOne
    @JoinColumn(name = "id_khoa")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bang_cap")
    private Degree degree;

    @Column(name = "so_nam_kinh_nghiem")
    private Integer exp;

    @Column(name = "chuc_danh")
    private String position;

    @Column(name = "trang_thai")
    private Boolean status = true;


    @Override
    public String toString() {
        return "";
    }
}
