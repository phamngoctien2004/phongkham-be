package com.dcm.demo.config.aes;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableConfigurationProperties(CryptoProps.class)
public class CryptoConfig {
    @Bean
    public SecretKey aesKey(CryptoProps props) {
        byte[] key = Base64.getDecoder().decode(props.getKeyBase64());
        if (key.length != 32) {
            throw new IllegalArgumentException("Yêu cầu khoá AES 256-bit (32 bytes).");
        }
        return new SecretKeySpec(key, "AES");
    }

    @Bean
    public AesGcmCrypto aesGcmCrypto(SecretKey aesKey) {
        return new AesGcmCrypto(aesKey);
    }
}
