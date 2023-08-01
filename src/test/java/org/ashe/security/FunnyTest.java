package org.ashe.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.auth.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class FunnyTest {

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void funny(){
        Assertions.assertDoesNotThrow(() -> {
            String jsonStr = objectMapper.writeValueAsString(RegisterRequest.builder()
                    .email("ashet@qq.com")
                    .password("1234")
                    .firstname("Ashe")
                    .lastname("Red")
                    .build());
            log.info(jsonStr);
        });
    }
}
