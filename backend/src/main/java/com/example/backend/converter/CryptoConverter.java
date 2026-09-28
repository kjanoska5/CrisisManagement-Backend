package com.example.backend.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Component
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String alg = "AES";
    private static String encryptionKey;

    // wczytywanie
    @Value("${app.security.encryption-key}")
    public void setEncryptionKey(String key) {
        CryptoConverter.encryptionKey = key;
    }

    @Override
    public String convertToDatabaseColumn(String data) {
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = new SecretKeySpec(encryptionKey.getBytes(), alg);
            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // szyfrowanie
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String data) {
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = new SecretKeySpec(encryptionKey.getBytes(), alg);
            Cipher cipher = Cipher.getInstance(alg);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // deszyfrowanie
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(data));
            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}