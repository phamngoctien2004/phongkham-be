package com.dcm.demo.config.aes;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false) // false: bạn gắn @Convert cho từng trường; true: áp dụng cho mọi String (cẩn thận!)
public class CryptoConverter implements AttributeConverter<String, String> {

    private AesGcmCrypto crypto;

    // Lấy bean lazy lần đầu dùng
    private AesGcmCrypto getCrypto() {
        if (crypto == null) {
            crypto = ApplicationContextProvider.getBean(AesGcmCrypto.class);
        }
        return crypto;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            // AAD có thể là tên entity|field, hoặc null
            return getCrypto().encryptToBase64(attribute, null);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể mã hoá trường", ex);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return getCrypto().decryptFromBase64(dbData, null);
        } catch (Exception ex) {
            throw new IllegalStateException("Không thể giải mã trường", ex);
        }
    }
}
