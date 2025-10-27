package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "phong_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ten_phong", nullable = false)
    private String name;

    @OneToOne
    @JoinColumn(name = "id_benh_nhan", nullable = false)
    private User patient;

    @Column(name = "nguoi_phan_hoi", nullable = false)
    private String responder;

    @Column(name = "last_read_patient")
    private Integer lastReadPatient;
    @Column(name = "last_read_ad")
    private Integer lastReadAdmin;

    @Column(name = "last_message")
    private Integer lastMessage;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}

