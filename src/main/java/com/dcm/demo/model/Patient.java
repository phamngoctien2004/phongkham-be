package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "benh_nhan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_benh_nhan")
    private Integer id;

    @Column(name = "ma_benh_nhan", unique = true, length = 20)
    private String code;

    @Column(name = "ho_ten")
    private String fullName;

    @Column(name = "sdt", length = 20)
    private String phone;

    @Column(name = "dia_chi", columnDefinition = "TEXT")
    private String address;

    @Column(name = "CCCD", columnDefinition = "TEXT")
    private String cccd;

    @Column(name = "ngay_sinh")
    private LocalDate birth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh")
    private User.Gender gender;

    @Column(name = "nhom_mau", length = 10)
    private String bloodType;

    @Column(name = "chieu_cao", precision = 5, scale = 2)
    private BigDecimal height; // in cm

    @Column(name = "can_nang", precision = 5, scale = 2)
    private BigDecimal weight; // in kg

    @Column(name = "anh_nguoi_dung", length = 500)
    private String profileImage;

    @CreationTimestamp
    @Column(name = "ngay_dang_ky")
    private LocalDateTime registrationDate;

    @Column(name = "sync")
    private String sync;
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Relationship> relationships = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> medicalRecords = new ArrayList<>();


    @Override
    public String toString() {
        return "";
    }

    public void addLink(Relationship r) {
        relationships.add(r);
        r.setPatient(this);
    }
    public void removeLink(Relationship r) {
        relationships.remove(r);
        r.setPatient(null);
    }
}
