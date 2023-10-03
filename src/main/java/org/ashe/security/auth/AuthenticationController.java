package org.ashe.security.auth;

import lombok.RequiredArgsConstructor;
import org.ashe.security.domain.Limit;
import org.ashe.security.domain.MobileDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    /**
     * 发送验证码
     */
    @PostMapping("/send")
    @Limit(threshold = 5, reset = 5) // (1小时内)某个ip连续请求次数 > 5次后拒绝，5分钟后解除限制
    public ResponseEntity<Void> sendCode(@RequestBody MobileDTO dto){
        service.sendCode(dto.getMobile());
        return ResponseEntity.ok().build();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    /**
     * 账号密码登录
     */
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * 手机验证码登录
     * @param verifyCode 验证码
     */
    @PostMapping("/code/{verifyCode}")
    public ResponseEntity<AuthenticationResponse> verifyCode(@PathVariable String verifyCode) {
        return ResponseEntity.ok(service.verifyCode(verifyCode));
    }

    /**
     * 钉钉扫码登录
     * <a href="http://127.0.0.1:8080/code.html">Ding login</a>
     * <a href="https://open.dingtalk.com/document/orgapp/tutorial-obtaining-user-personal-information#title-ts9-exq-xrh">reference</a>
     */
    @GetMapping("/ding")
    public ResponseEntity<AuthenticationResponse> ding(@RequestParam(value = "authCode") String authCode) {
        return ResponseEntity.ok(service.ding(authCode));
    }


}
