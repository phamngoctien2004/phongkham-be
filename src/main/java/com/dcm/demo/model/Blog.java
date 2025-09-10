package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_blog")
    private Integer blogId;

    @Column(name = "tieu_de", nullable = false)
    private String title;

    @Column(name = "noi_dung", columnDefinition = "TEXT")
    private String content;

    @Column(name = "ngay_dang")
    private LocalDate publishDate;

    @Column(name = "tac_gia")
    private String author;

    @Column(name = "hinh_anh")
    private String image;

    @Column(name = "trang_thai", length = 50)
    private String status;
    @Override
    public String toString() {
        return "";
    }
}
