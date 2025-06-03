package edu.bilak.setdemo.service;

import edu.bilak.setdemo.exception.IntegrityViolationException;
import edu.bilak.setdemo.exception.ParticipantKeyNotFoundException;
import edu.bilak.setdemo.model.SETMessage;
import edu.bilak.setdemo.util.CryptoUtil;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yuliana
 * @version 1.0.0
 * @project set-demo
 * @class SETProtocolService
 * @since 03/06/2025 — 19.09
 **/
@Service
public class SETProtocolService {
    private final CryptoUtil cryptoUtil;
    private final Map<String, KeyPair> keyPairs = new HashMap<>();
    private boolean useOAEP = false;

    public SETProtocolService(CryptoUtil cryptoUtil) {
        this.cryptoUtil = cryptoUtil;
    }

    public void initializeKeys(String participantName) throws Exception {
        KeyPair keyPair = cryptoUtil.generateRSAKeyPair();
        keyPairs.put(participantName, keyPair);
        cryptoUtil.saveKeyPairToFlash(keyPair, participantName);
    }

    public void loadKeys(String participantName) throws Exception {
        KeyPair keyPair = cryptoUtil.loadKeyPairFromFlash(participantName);
        keyPairs.put(participantName, keyPair);
    }

    public void setOAEPEnabled(boolean enabled) {
        this.useOAEP = enabled;
    }

    public SETMessage createSecureMessage(String senderName, String message, String recipientName) throws GeneralSecurityException {
        if (!keyPairs.containsKey(senderName)) {
            throw new ParticipantKeyNotFoundException(senderName);
        }
        if (!keyPairs.containsKey(recipientName)) {
            throw new ParticipantKeyNotFoundException(recipientName);
        }

        KeyPair senderKeys = keyPairs.get(senderName);
        PublicKey recipientPublicKey = keyPairs.get(recipientName).getPublic();

        // 1. Початкове повідомлення
        byte[] originalData = message.getBytes(StandardCharsets.UTF_8);

        // 2. SHA хешування
        byte[] messageHash = cryptoUtil.calculateSHA256(originalData);

        // 3. Генерація сесійного ключа DES
        SecretKey sessionKey = cryptoUtil.generateDESKey();

        // 4. DES шифрування повідомлення
        byte[] encryptedMessage = cryptoUtil.encryptDES(originalData, sessionKey);

        // 5. RSA шифрування сесійного ключа DES (з можливістю OAEP)
        byte[] encryptedSessionKey = cryptoUtil.encryptRSA(sessionKey.getEncoded(), recipientPublicKey, useOAEP);

        // 6. Цифровий підпис хешу повідомлення
        byte[] signature = cryptoUtil.signRSA(messageHash, senderKeys.getPrivate());

        // 7. HMAC для аутентифікації
        SecretKey hmacKey = new SecretKeySpec(sessionKey.getEncoded(), "HmacSHA256");
        byte[] hmac = cryptoUtil.hmac(encryptedMessage, hmacKey);

        return new SETMessage("SECURE_MESSAGE", encryptedMessage, signature, encryptedSessionKey, hmac, senderName, LocalDateTime.now());
    }

    public String processSecureMessage(SETMessage setMessage, String recipientName) throws GeneralSecurityException {
        if (!keyPairs.containsKey(recipientName)) {
            throw new ParticipantKeyNotFoundException(recipientName);
        }

        KeyPair recipientKeys = keyPairs.get(recipientName);
        String senderName = setMessage.getSenderInfo();

        if (!keyPairs.containsKey(senderName)) {
            throw new ParticipantKeyNotFoundException(senderName);
        }

        PublicKey senderPublicKey = keyPairs.get(senderName).getPublic();

        // 1. Розшифрування сесійного ключа DES
        byte[] sessionKeyBytes = cryptoUtil.decryptRSA(setMessage.getEncryptedSessionKey(),
                recipientKeys.getPrivate(), useOAEP);
        SecretKey sessionKey = new SecretKeySpec(sessionKeyBytes, "DES");

        // 2. Перевірка HMAC
        SecretKey hmacKey = new SecretKeySpec(sessionKey.getEncoded(), "HmacSHA256");
        if (!cryptoUtil.verifyHmac(setMessage.getEncryptedData(), setMessage.getHmac(), hmacKey)) {
            throw new IntegrityViolationException("ПОРУШЕННЯ ЦІЛІСНОСТІ: HMAC не збігається!");
        }

        // 3. Розшифрування повідомлення
        byte[] decryptedMessage = cryptoUtil.decryptDES(setMessage.getEncryptedData(), sessionKey);

        // 4. Перевірка цифрового підпису
        byte[] messageHash = cryptoUtil.calculateSHA256(decryptedMessage);
        if (!cryptoUtil.verifyRSASignature(messageHash, setMessage.getSignature(), senderPublicKey)) {
            throw new IntegrityViolationException("ПОРУШЕННЯ ЦІЛІСНОСТІ: Цифровий підпис недійсний!");
        }

        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }

    // Отримання публічного ключа учасника
    public String getPublicKeyBase64(String participantName) {
        if (keyPairs.containsKey(participantName)) {
            return Base64.getEncoder().encodeToString(keyPairs.get(participantName).getPublic().getEncoded());
        }
        return null;
    }
}
