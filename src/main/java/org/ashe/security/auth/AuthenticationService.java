package org.ashe.security.auth;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.RequiredArgsConstructor;
import org.ashe.security.config.JwtService;
import org.ashe.security.infra.ServiceException;
import org.ashe.security.user.Role;
import org.ashe.security.user.User;
import org.ashe.security.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        Optional<User> optionalUser = repository.findByMobile(request.getEmail());
        if (optionalUser.isPresent()) {
            throw new ServiceException(String.format("this email %s has been registered", request.getEmail()));
        }
        var user = User.builder()
                .mobile(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(Role.USER)
                .createTime(new Date())
                .updateTime(new Date())
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticateRequest request) {
        // 鉴权
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByMobile(request.getEmail())
                .orElseThrow();
        // 授权
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(String authCode) {
        // 钉钉扫码鉴权
        String mobile = getMobile(authCode);
        var user = repository.findByMobile(mobile)
                .orElseThrow();
        // 授权
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String getMobile(String authCode) {
        try {
            Config config = new Config();
            config.protocol = "https";
            config.regionId = "central";
            Client client = new Client(config);
            GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()

                    // 应用基础信息-应用信息的AppKey,请务必替换为开发的应用AppKey
                    .setClientId("dingrbbhg7plnul8namp")

                    // 应用基础信息-应用信息的AppSecret，,请务必替换为开发的应用AppSecret
                    .setClientSecret("4ReGK6uU8tZ1TZwQEOai5lXTLpZDeN2BJ-Wi6yJxNylxdJ4paTjfPjxnBEhI5SDE")
                    .setCode(authCode)
                    .setGrantType("authorization_code");
            GetUserTokenResponse getUserTokenResponse = client.getUserToken(getUserTokenRequest);
            // 获取用户个人token
            String accessToken = getUserTokenResponse.getBody().getAccessToken();
            Config config1 = new Config();
            config1.protocol = "https";
            config1.regionId = "central";
            com.aliyun.dingtalkcontact_1_0.Client client1 = new com.aliyun.dingtalkcontact_1_0.Client(config1);
            GetUserHeaders getUserHeaders = new GetUserHeaders();
            getUserHeaders.xAcsDingtalkAccessToken = accessToken;
            GetUserResponse userResponse = client1.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
            return userResponse.getBody().getMobile();
        } catch (Exception e) {
            throw new UsernameNotFoundException("FBI");
        }
    }
}
