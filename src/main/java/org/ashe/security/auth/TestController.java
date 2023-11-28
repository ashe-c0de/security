package org.ashe.security.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 已在SecurityConf放行此接口层
 * 无需token即可访问
 */
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final TestService testService;


    @GetMapping("/hello")
    public String hello() {
        return testService.hello();
    }

    /**
     * 钉钉机器人webhook实时报警
     */
    @GetMapping("/dingTalk")
    public void dingTalk() {
        testService.dingTalk();
    }

    /**
     * RabbitMQ confirm 模式
     */
    @GetMapping("/mq")
    public void mq() {
        testService.mq();
    }

    @PostMapping("/aspect")
    public void aspectException() {
        testService.aspectException();
    }
}
