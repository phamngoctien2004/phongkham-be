package com.dcm.demo.config.aes;

import com.google.crypto.tink.CleartextKeysetHandle;
import com.google.crypto.tink.DeterministicAead;
import com.google.crypto.tink.JsonKeysetReader;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.config.TinkConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Configuration
public class CryptoConfig {
    @Value("${security.crypto.aes.key-base64}")
    private String aesKeyBase64;
//
//    @Bean
//    public SecretKey aesKey() {
//        byte[] key = Base64.getDecoder().decode(aesKeyBase64);
//        if (key.length != 32) {
//            throw new IllegalArgumentException("Yêu cầu khoá AES 256-bit (32 bytes).");
//        }
//        return new SecretKeySpec(key, "AES");
//    }
//
//    @Bean
//    public AesGcmCrypto aesGcmCrypto(SecretKey aesKey) {
//        return new AesGcmCrypto(aesKey);
//    }

    @Bean
    public DeterministicAead deterministicAead() throws GeneralSecurityException, IOException {
        TinkConfig.register();
        String keysetJson = "{\"primaryKeyId\":1032924696,\"key\":[{\"keyData\":{\"typeUrl\":\"type.googleapis.com/google.crypto.tink.AesSivKey\",\"value\":\"EkCJQD81yUnGiDvzZrVJDTsdyB9I2hZ/CLHjeXa5wh9w2hLknNLXjJpq+aU0VqBKvzKR/5JZ+tCSkurGKw9AAN6G\",\"keyMaterialType\":\"SYMMETRIC\"},\"status\":\"ENABLED\",\"keyId\":1032924696,\"outputPrefixType\":\"TINK\"}]}\n";
        KeysetHandle handle = CleartextKeysetHandle.read(JsonKeysetReader.withString(keysetJson));
        return handle.getPrimitive(DeterministicAead.class);
    }

}
