package org.ashe.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.auth.RegisterRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * spring.profiles.active=dev
 */
@SpringBootTest
@Slf4j
class FunnyTest {

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void funny() {
        Assertions.assertDoesNotThrow(() -> {
            String jsonStr = objectMapper.writeValueAsString(RegisterRequest.builder()
                    .mobile("ashet@qq.com")
                    .password("1234")
                    .firstname("Ashe")
                    .lastname("Red")
                    .build());
            log.info(jsonStr);
        });
    }

    @Test
    void collection() {
        Assertions.assertDoesNotThrow(() -> {
            List<String> duplicateElements = getDuplicateElements(List.of("111", "222", "333"));
            log.info("duplicateElements: {}", duplicateElements);
        });
    }

    @Test
    void hashMap() {
        Assertions.assertDoesNotThrow(() -> {
            Map<String, Object> hashMap = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                hashMap.put(String.valueOf(i), String.valueOf(i));
            }
            for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
                log.info("key : {}", entry.getKey());
                log.info("value : {}", entry.getValue().toString());
            }
        });
    }

    public List<String> getDuplicateElements(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return List.of();
        }
        Set<String> set = new HashSet<>();
        return list.stream()
                .filter(e -> !set.add(e))
                .toList();
    }

    @Test
    void identity() {
        Assertions.assertDoesNotThrow(() -> {
            String identity = "513023199802215336";
            String formatIdentity = identity.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
            log.info(formatIdentity);
        });
    }

}
