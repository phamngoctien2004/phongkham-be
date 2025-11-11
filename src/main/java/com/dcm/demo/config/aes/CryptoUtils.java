package com.dcm.demo.config.aes;

import com.google.crypto.tink.DeterministicAead;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class CryptoUtils {
    private static final byte[] AAD = new byte[0];

    public static String encrypt(String plainText)   {
        try{
            DeterministicAead daead = ApplicationContextProvider.getBean(DeterministicAead.class);

            // Mã hoá lại giá trị phone (dùng đúng AAD hoặc null)
            byte[] cipher = daead.encryptDeterministically(
                    plainText.getBytes(StandardCharsets.UTF_8), AAD// hoặc new byte[0] nếu không dùng AAD
            );

            return Base64.getEncoder().encodeToString(cipher);
        }catch (Exception e){
            throw new IllegalStateException("Encrypt error", e);
        }
    }
}
