package org.ashe.security.auth;

import org.ashe.security.domain.Auth;
import org.ashe.security.user.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
public class TokenController {

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/admin")
    @Auth(value = Role.ADMIN) // ADMIN角色才有访问权限
    public String admin() {
        return "hello admin";
    }
}
