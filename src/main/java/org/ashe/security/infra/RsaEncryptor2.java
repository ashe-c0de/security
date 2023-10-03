package org.ashe.security.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA算法    非对称加密解密（公钥用于加密，私钥用于解密）
 * 固定密钥对
 */
@Component
@RequiredArgsConstructor
public class RsaEncryptor2 {

    /**
     * 读取配置文件的公钥 & 私钥
     */
    private final ConfValue confValue;

    private static final String CIPHER_ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";


    /**
     * 加密
     *
     * @param message 明文
     * @return 密文
     */
    public String encrypt(String message) {
        try {
            // 公钥加密
            byte[] publicKeyBytes = Base64.getDecoder().decode(confValue.getPublicKey());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(CIPHER_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] ciphertext = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new ServiceException("加密发生异常", e);
        }
    }

    /**
     * 解密
     *
     * @param ciphertext 密文
     * @return 解密后的明文
     */
    public String decrypt(String ciphertext) {
        try {
            // 私钥解密
            byte[] privateKeyBytes = Base64.getDecoder().decode(confValue.getPrivateKey());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(CIPHER_ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] plaintext = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ServiceException("解密发生异常", e);
        }
    }
}