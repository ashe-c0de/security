package org.ashe.security.auth;

import com.aliyun.dingtalkcontact_1_0.models.GetUserHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponse;
import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.config.JwtService;
import org.ashe.security.domain.RedisKey;
import org.ashe.security.infra.ConfValue;
import org.ashe.security.infra.RsaEncryptor2;
import org.ashe.security.infra.ServiceException;
import org.ashe.security.user.Role;
import org.ashe.security.user.User;
import org.ashe.security.user.UserRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository repository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RsaEncryptor2 rsaEncryptor2;
    private final ConfValue confValue;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String FBI = "FBI";
    private static final String HTTPS = "https";
    private static final String CENTRAL = "central";

    public AuthenticationResponse register(RegisterRequest request) {
        Optional<User> optionalUser = repository.findByMobile(request.getMobile());
        if (optionalUser.isPresent()) {
            throw new ServiceException(String.format("this mobile %s has been registered", request.getMobile()));
        }
        var user = User.builder()
                .mobile(request.getMobile())
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
        // 账号密码验证
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMobile(),
                        request.getPassword()
                )
        );
        var user = repository.findByMobile(request.getMobile())
                .orElseThrow(() -> new UsernameNotFoundException(FBI));
        // 授权
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse ding(String authCode) {
        // 钉钉扫码
        String mobile = getMobile(authCode);
        var user = repository.findByMobile(mobile)
                .orElseThrow(() -> new ServiceException("register first please"));
        log.info("mobile --> {}", mobile);
        // 授权
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private String getMobile(String authCode) {
        try {
            Config config = new Config();
            config.protocol = HTTPS;
            config.regionId = CENTRAL;
            Client client = new Client(config);
            GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()

                    // 应用基础信息-应用信息的AppKey,请务必替换为开发的应用AppKey
                    .setClientId(rsaEncryptor2.decrypt(confValue.getAppKey()))

                    // 应用基础信息-应用信息的AppSecret，,请务必替换为开发的应用AppSecret
                    .setClientSecret(rsaEncryptor2.decrypt(confValue.getAppSecret()))

                    .setCode(authCode)

                    .setGrantType("authorization_code");
            GetUserTokenResponse getUserTokenResponse = client.getUserToken(getUserTokenRequest);
            // 获取用户个人token
            String accessToken = getUserTokenResponse.getBody().getAccessToken();
            Config config1 = new Config();
            config1.protocol = HTTPS;
            config1.regionId = CENTRAL;
            com.aliyun.dingtalkcontact_1_0.Client client1 = new com.aliyun.dingtalkcontact_1_0.Client(config1);
            GetUserHeaders getUserHeaders = new GetUserHeaders();
            getUserHeaders.xAcsDingtalkAccessToken = accessToken;
            GetUserResponse userResponse = client1.getUserWithOptions("me", getUserHeaders, new RuntimeOptions());
            return userResponse.getBody().getMobile();
        } catch (Exception e) {
            throw new UsernameNotFoundException(FBI);
        }
    }

    public AuthenticationResponse verifyCode(String verifyCode) {
        // 核对验证码
        String mobile = stringRedisTemplate.opsForValue().get(RedisKey.getKey(RedisKey.SMS_VERIFY_CODE, verifyCode));
        Assert.isTrue(!Objects.isNull(mobile), "verifyCode is expired or wrong");
        // 查找用户或自动注册
        User user = repository.findByMobile(mobile)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .mobile(mobile)
                            .password(passwordEncoder.encode("1234"))
                            .role(Role.USER)
                            .createTime(new Date())
                            .updateTime(new Date())
                            .build();
                    return repository.save(newUser);
                });

        // 生成 JWT 令牌
        String jwtToken = jwtService.generateToken(user);
        // 授权
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void sendCode(String mobile) {
        // 验证码------6位UUID随机数字
        String str = Integer.toString(UUID.randomUUID().hashCode());
        String verifyCode = str.substring(str.length() - 6);
        log.info("verifyCode ----> {}", verifyCode);
        String key = RedisKey.getKey(RedisKey.SMS_VERIFY_CODE, verifyCode);
        stringRedisTemplate.opsForValue().set(key, mobile, 5, TimeUnit.MINUTES);
    }
}
