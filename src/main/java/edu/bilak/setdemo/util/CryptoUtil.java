package edu.bilak.setdemo.util;

import edu.bilak.setdemo.exception.CryptoException;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.security.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class CryptoUtil
 * @since 03/06/2025 — 19.09
 **/
@Component
public class CryptoUtil {
    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION_PKCS1 = "RSA/ECB/PKCS1Padding";
    private static final String RSA_TRANSFORMATION_OAEP = "RSA/ECB/OAEPPadding";

    private static final String DES_ALGORITHM = "DES";
    private static final String DES_TRANSFORMATION = "DES/CBC/PKCS5Padding";

    private static final String SHA_ALGORITHM = "SHA-256";
    private static final String HMAC_SHA_ALGORITHM = "HmacSHA256";

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private static final int RSA_KEY_SIZE = 2048;   // бітів
    private static final int DES_KEY_SIZE = 56;     // бітів
    private static final int DES_BLOCK_SIZE = 8;    // байтів (також розмір IV)

    // Генерація пари ключів RSA
    public KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyGen.initialize(RSA_KEY_SIZE);
            return keyGen.generateKeyPair();
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Не вдалось згенерувати RSA-пару", e);
        }
    }

    // Генерація сесійного ключа DES
    public SecretKey generateDESKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(DES_ALGORITHM);
            keyGen.init(DES_KEY_SIZE);
            return keyGen.generateKey();
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Не вдалось згенерувати DES-ключ", e);
        }
    }

    // Збереження ключів на "флеш-диск" (локальну папку)
    public void saveKeyPairToFlash(KeyPair keyPair, String fileName) throws Exception {
        Path flashDir = Paths.get("flash_keys");
        Files.createDirectories(flashDir);

        // Збереження публічного ключа
        String publicKeyB64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        Files.write(flashDir.resolve(fileName + "_public.key"), publicKeyB64.getBytes());

        // Збереження приватного ключа
        String privateKeyB64 = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        Files.write(flashDir.resolve(fileName + "_private.key"), privateKeyB64.getBytes());
    }

    // Завантаження ключів з "флеш-диска"
    public KeyPair loadKeyPairFromFlash(String fileName) throws Exception {
        Path flashDir = Paths.get("flash_keys");

        // Завантаження публічного ключа
        String publicKeyB64 = new String(Files.readAllBytes(flashDir.resolve(fileName + "_public.key")));
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyB64);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        // Завантаження приватного ключа
        String privateKeyB64 = new String(Files.readAllBytes(flashDir.resolve(fileName + "_private.key")));
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyB64);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        return new KeyPair(publicKey, privateKey);
    }

    // SHA хешування
    public byte[] calculateSHA256(byte[] data) throws GeneralSecurityException {
        return MessageDigest.getInstance(SHA_ALGORITHM).digest(data);
    }

    // Шифрування даних RSA: з падінгом PKCS#1 або OAEP (SHA-256 + MGF1)
    public byte[] encryptRSA(byte[] data, PublicKey key, boolean oaep) {
        return oaep ? encryptRSA_OAEP(data, key) : encryptRSA_PKCS1(data, key);
    }

    // Розшифровує дані RSA: з PKCS#1 чи OAEP
    public byte[] decryptRSA(byte[] enc, PrivateKey key, boolean oaep) {
        return oaep ? decryptRSA_OAEP(enc, key) : decryptRSA_PKCS1(enc, key);
    }

    private byte[] encryptRSA_PKCS1(byte[] data, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка RSA/PKCS1", e);
        }
    }

    private byte[] decryptRSA_PKCS1(byte[] enc, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(enc);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка дешифрування RSA/PKCS1", e);
        }
    }

    private byte[] encryptRSA_OAEP(byte[] data, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION_OAEP);
            OAEPParameterSpec spec = new OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT
            );
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);
            return cipher.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка RSA/OAEP", e);
        }
    }

    private byte[] decryptRSA_OAEP(byte[] enc, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION_OAEP);
            OAEPParameterSpec spec = new OAEPParameterSpec(
                    SHA_ALGORITHM, "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);
            return cipher.doFinal(enc);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка дешифрування RSA/OAEP", e);
        }
    }

    // Шифрування даних DES-ключом у режимі CBC з автогенерацією IV
    public byte[] encryptDES(byte[] data, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);                // генерується випадковий IV
            byte[] iv = cipher.getIV();                           // 8 байтів
            byte[] ct = cipher.doFinal(data);

            // [IV ‖ CIPHER]
            byte[] out = new byte[iv.length + ct.length];
            System.arraycopy(iv, 0, out, 0, iv.length);
            System.arraycopy(ct, 0, out, iv.length, ct.length);
            return out;
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка DES-шифрування", e);
        }
    }

    // Дешифрування DES-повідомлення, в якому перші 8 байт - IV
    public byte[] decryptDES(byte[] ivAndCipher, SecretKey key) {
        if (ivAndCipher.length < DES_BLOCK_SIZE) {
            throw new CryptoException("Довжина шифртексту менша за розмір IV");
        }
        try {
            byte[] iv = new byte[DES_BLOCK_SIZE];
            byte[] ct = new byte[ivAndCipher.length - DES_BLOCK_SIZE];
            System.arraycopy(ivAndCipher, 0, iv, 0, DES_BLOCK_SIZE);
            System.arraycopy(ivAndCipher, DES_BLOCK_SIZE, ct, 0, ct.length);

            Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            return cipher.doFinal(ct);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка DES-дешифрування", e);
        }
    }

    // Створення цифрового підпису RSA
    public byte[] signRSA(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка створення цифрового підпису", e);
        }
    }

    // Перевірка цифрового підпису RSA
    public boolean verifyRSASignature(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка перевірки цифрового підпису", e);
        }
    }

    // Обчислює HMAC від даних
    public byte[] hmac(byte[] data, SecretKey hmacKey) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_ALGORITHM);
            mac.init(hmacKey);
            return mac.doFinal(data);
        } catch (GeneralSecurityException e) {
            throw new CryptoException("Помилка обчислення HMAC", e);
        }
    }

    // Перевіряє чи співпадає очікуваний HMAC з обчисленим
    public boolean verifyHmac(byte[] data, byte[] expected, SecretKey hmacKey) {
        return MessageDigest.isEqual(hmac(data, hmacKey), expected);
    }
}
