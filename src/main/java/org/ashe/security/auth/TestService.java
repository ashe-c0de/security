package org.ashe.security.auth;

import lombok.extern.slf4j.Slf4j;
import org.ashe.security.infra.EmergencyException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestService {


    public void dingTalk() {
        try {
            int a = 1;
            int b = 0;
            int c = a / b;
            log.info("c = {}", c);
        } catch (Exception e) {
            throw new EmergencyException(e, "15283871282", "xx业务xx功能发生异常");
        }
    }

}
