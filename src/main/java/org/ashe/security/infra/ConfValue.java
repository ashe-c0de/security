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

    @Value("${rsa.public_key}")
    private String publicKey;

    @Value("${rsa.private_key}")
    private String privateKey;

    @Value("${oauth2.secret-key}")
    private String secretKey;

}
