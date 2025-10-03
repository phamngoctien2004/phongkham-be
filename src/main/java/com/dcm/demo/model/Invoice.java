package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hoa_don_thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoa_don")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_benh_nhan", nullable = false)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_kham", nullable = false)
    private MedicalRecord medicalRecord;

    @Column(name = "ma_hoa_don", unique = true, length = 50)
    private String code;

    @Column(name = "tong_tien", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "so_tien_thanh_toan", nullable = false, precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuong_thuc")
    private PaymentMethod paymentMethod;

    @CreationTimestamp
    @Column(name = "ngay_thanh_toan")
    private LocalDateTime paymentDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_thu_tien")
    private User cashier;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private PaymentStatus status = PaymentStatus.CHUA_THANH_TOAN;

    @Column(name = "ghi_chu", columnDefinition = "TEXT")
    private String note;

    @Column(name = "payos_order")
    private Long payosOrder;
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
    public enum PaymentMethod {
        TIEN_MAT, CHUYEN_KHOAN
    }

    public enum PaymentStatus {
        CHUA_THANH_TOAN, DA_THANH_TOAN, THANH_TOAN_MOT_PHAN
    }
    @Override
    public String toString() {
        return "";
    }
}
