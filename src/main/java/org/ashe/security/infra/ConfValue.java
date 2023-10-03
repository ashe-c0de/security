package org.ashe.security.infra;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件的值
 */
@Component
@Getter
public class ConfValue {

    /**
     * 固定公钥，用以加密敏感信息
     */
    @Value("${rsa.public_key}")
    private String publicKey;

    /**
     * 固定私钥，用以解密敏感信息
     */
    @Value("${rsa.private_key}")
    private String privateKey;

    /**
     * 颁发jwtToken的秘钥（已加密）
     */
    @Value("${oauth2.secret-key}")
    private String secretKey;

    /**
     * 钉钉登录的AppKey（已加密）
     */
    @Value("${ding-talk.app-key}")
    private String appKey;

    /**
     * 钉钉登录的AppSecret（已加密）
     */
    @Value("${ding-talk.app-secret}")
    private String appSecret;

}
