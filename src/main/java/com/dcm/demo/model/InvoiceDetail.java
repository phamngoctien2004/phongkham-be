package com.dcm.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_hoa_don")
@Data
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_hoa_don", nullable = false)
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "id_dich_vu", nullable = false)
    private HealthPlan healthPlan;

    @Column(name = "phi_dich_vu")
    private BigDecimal fee = BigDecimal.ZERO;
    @Column(name = "so_tien_thanh_toan")
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(name = "phuong_thuc_thanh_toan")
    private Invoice.PaymentMethod paymentMethod;

    @Column(name = "mo_ta")
    private String description;
}
