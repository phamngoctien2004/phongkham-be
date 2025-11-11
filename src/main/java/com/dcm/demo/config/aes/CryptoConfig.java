package com.dcm.demo.config.aes;

import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.daead.DeterministicAeadKeyTemplates;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
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

    @Bean
    public DeterministicAead deterministicAead() throws GeneralSecurityException, IOException {
        com.google.crypto.tink.config.TinkConfig.register();
        // Keyset có thể nằm trong Secret Manager / ENV / file được mã hoá
        KeysetHandle handle = KeysetHandle.generateNew(DeterministicAeadKeyTemplates.AES256_SIV);
        return handle.getPrimitive(DeterministicAead.class);
    }
}
