package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan_anh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_tin_nhan", nullable = false)
    private Message message;

    @Column(name = "url", nullable = false)
    private String url;
}
