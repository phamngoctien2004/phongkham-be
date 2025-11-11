package com.dcm.demo.config.aes;

import com.google.crypto.tink.DeterministicAead;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

@Converter(autoApply = false)
public class CryptoConverter implements AttributeConverter<String, String> {

    private DeterministicAead daead; // AES-SIV
    private static final byte[] AAD = new byte[0];

    private DeterministicAead p() {
        if (daead == null) daead = ApplicationContextProvider.getBean(DeterministicAead.class);
        return daead;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            byte[] ct = p().encryptDeterministically(attribute.getBytes(StandardCharsets.UTF_8), AAD);

            return Base64.getEncoder().encodeToString(ct); // không cần IV, chỉ Base64(ct)
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encrypt error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            byte[] pt = p().decryptDeterministically(Base64.getDecoder().decode(dbData), AAD);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Decrypt error", e);
        }
    }


}
