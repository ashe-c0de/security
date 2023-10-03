package org.ashe.security;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.infra.RsaEncryptor2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class RsaEncryptor2Test {

    @Resource
    private RsaEncryptor2 rsaEncryptor2;

    private static String value;

    @Test
    void encrypt() {
        Assertions.assertDoesNotThrow(() -> {
            String encrypt = rsaEncryptor2.encrypt("4ReGK6uU8tZ1TZwQEOai5lXTLpZDeN2BJ-Wi6yJxNylxdJ4paTjfPjxnBEhI5SDE");
            value = encrypt;
            log.info("-----------> " + encrypt);
            String password = rsaEncryptor2.decrypt(value);
            log.info("-----------> " + password);
        });
    }

}