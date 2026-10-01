package com.example.backend.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static String encryptionKey;
    private final SecureRandom secureRandom = new SecureRandom();

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
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);

            Key secretKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] encryptedData = cipher.doFinal(data.getBytes());

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedData);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Błąd szyfrowania danych medycznych", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String data) {
        if (data == null) {
            return null;
        }
        try {
            byte[] decodedData = Base64.getDecoder().decode(data);
            byte[] iv = new byte[IV_LENGTH];
            System.arraycopy(decodedData, 0, iv, 0, iv.length);

            byte[] encryptedData = new byte[decodedData.length - iv.length];
            System.arraycopy(decodedData, iv.length, encryptedData, 0, encryptedData.length);

            // desczyfr init z IV
            Key secretKey = new SecretKeySpec(encryptionKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // deszczyfr.
            byte[] decryptedData = cipher.doFinal(encryptedData);
            return new String(decryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Błąd deszyfrowania danych medycznych", e);
        }
    }
}