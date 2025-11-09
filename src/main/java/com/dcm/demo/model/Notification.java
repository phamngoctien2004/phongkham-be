package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "thong_bao")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tieu_de")
    private String title;

    @Column(name = "thoi_gian_tao")
    @CreationTimestamp
    private LocalDateTime time;

    @Column(name = "is_user_read")
    private Boolean isUserRead = false;
    @Column(name = "is_admin_read")
    private Boolean isAdminRead = false;

    @Column(name = "nguoi_nhan")
    private Integer receiverId;
    @Column(name = "loai_id")
    private Integer typeId;
    @Column(name = "anh")
    private String image;
    @Column(name = "noi_dung")
    private String content;

    @Column(name = "loai_thong_bao")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public enum NotificationType {
        HE_THONG,
        DAT_LICH,
    }
}
