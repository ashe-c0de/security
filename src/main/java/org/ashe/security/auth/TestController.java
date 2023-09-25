package org.ashe.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/dingTalk")
    public void dingTalk() {
        testService.dingTalk();
    }

    @GetMapping("/mq")
    public void mq() {
        testService.mq();
    }
}
