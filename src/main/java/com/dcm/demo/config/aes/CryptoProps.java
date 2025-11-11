package com.dcm.demo.config.aes;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.crypto.aes")
@Data
public class CryptoProps {
    private String keyBase64;
}
