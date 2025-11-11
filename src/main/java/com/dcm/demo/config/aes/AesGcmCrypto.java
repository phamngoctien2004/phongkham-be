package com.dcm.demo.config.aes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


public class AesGcmCrypto {
    private static final int IV_LEN = 12;       // 96-bit IV
    private static final int TAG_LEN_BIT = 128; // 128-bit auth tag
    private final SecretKey key;
    private final SecureRandom rnd = new SecureRandom();

    public AesGcmCrypto(SecretKey key) { this.key = key; }

    // Trả về dạng "Base64(iv):Base64(ciphertext)"
    public String encryptToBase64(String plaintext, String aad) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[IV_LEN];
            rnd.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BIT, iv));
            if (aad != null) cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
            byte[] ct = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(ct);
        } catch (Exception e) {
            throw new IllegalStateException("Lỗi mã hoá AES-GCM", e);
        }
    }

    public String decryptFromBase64(String combined, String aad) {
        if (combined == null) return null;
        try {
            String[] parts = combined.split(":");
            if (parts.length != 2) throw new IllegalArgumentException("Sai định dạng ciphertext");
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ct = Base64.getDecoder().decode(parts[1]);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LEN_BIT, iv));
            if (aad != null) cipher.updateAAD(aad.getBytes(StandardCharsets.UTF_8));
            byte[] pt = cipher.doFinal(ct);
            return new String(pt, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // AEADBadTagException → sai khoá/IV/aad/dữ liệu bị sửa đổi
            throw new IllegalStateException("Lỗi giải mã AES-GCM", e);
        }
    }
}
