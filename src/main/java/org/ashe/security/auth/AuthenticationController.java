package org.ashe.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    /**
     * <a href="http://127.0.0.1:8080/code.html">Ding login</a>
     * <a href="https://open.dingtalk.com/document/orgapp/tutorial-obtaining-user-personal-information#title-ts9-exq-xrh">reference</a>
     */
    @GetMapping("/ding")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestParam(value = "authCode") String authCode){
        return ResponseEntity.ok(service.authenticate(authCode));
    }


}
