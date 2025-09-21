package com.dcm.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class WebhookRequest {
    private String code;      // "00"
    private String desc;      // "success"
    private boolean success;

    private String signature; // chữ ký trong body (PayOS có thể gửi thêm header x-signature)

    @JsonProperty("data")
    private DataPayload data;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataPayload {
        @JsonProperty("orderCode")
        private Long orderCode;           // 123

        @JsonProperty("amount")
        private Long amount;              // 3000 (VND)

        private String description;       // "VQRIO123"
        private String accountNumber;
        private String reference;

        @JsonProperty("transactionDateTime")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Ho_Chi_Minh")
        private LocalDateTime transactionDateTime; // "2023-02-04 18:25:00"

        private String currency;          // "VND"
        private String paymentLinkId;
        private String paymentLink;       // (có thể không có trong webhook)
        private String status;            // (nếu có)
        private String code;              // "00" (lồng bên trong data)
        private String desc;              // "Thành công"

        private String counterAccountBankId;
        private String counterAccountBankName;
        private String counterAccountName;
        private String counterAccountNumber;
        private String virtualAccountName;
        private String virtualAccountNumber;
    }
}
