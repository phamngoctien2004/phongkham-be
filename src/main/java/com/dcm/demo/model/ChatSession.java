package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_session")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat_session")
    private Integer chatSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_admin", nullable = false)
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_benh_nhan", nullable = false)
    private Patient patient;

    @CreationTimestamp
    @Column(name = "ngay_bat_dau")
    private LocalDateTime startDate;

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime endDate;
    @Override
    public String toString() {
        return "";
    }
}
