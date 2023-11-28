package org.ashe.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ashe.security.domain.Auth;
import org.ashe.security.domain.Limit;
import org.ashe.security.domain.RedisKey;
import org.ashe.security.user.Role;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解统一切面
 * <p></p>
 * auth模块，所用自定义注解的切面逻辑统一写入此类
 */
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class CustomAspect {

    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;

    /*========================================= @Auth ==========================================*/

    /**
     * 权限注解切面逻辑实现
     */
    @Around("@annotation(org.ashe.security.domain.Auth)")
    public Object checkAuth(ProceedingJoinPoint joinPoint) throws Throwable {
        String role = jwtService.extractRole();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Auth annotation = method.getAnnotation(Auth.class);
        Assert.notNull(annotation, "Missing @Auth Annotation on target method");

        Role requiredRole = annotation.value();
        Assert.isTrue(requiredRole.name().equals(role), String.format("Only %s can request", requiredRole));
        return joinPoint.proceed();
    }

    /*========================================= @Limit ==========================================*/

    /**
     * 接口调用次数限制注解逻辑实现
     */
    @Around("@annotation(org.ashe.security.domain.Limit)")
    public Object limit(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 取出注解实例
        Limit limit = method.getAnnotation(Limit.class);
        Assert.notNull(limit, "Missing @Limit Annotation on target method");

        // 获取ipAddress
        String ipAddress = jwtService.getClientIpAddress();
        String key = RedisKey.getKey(RedisKey.REQUEST_COUNT, ipAddress);

        Assert.isTrue(limitIpCallThis(key), String.format("发送短信验证码的次数受限，请%s分钟后再试", stringRedisTemplate.getExpire(key, limit.unit())));
        return joinPoint.proceed();
    }

    private boolean limitIpCallThis(String key) {
        // key的值进行自增，初始值为1
        Long increment = stringRedisTemplate.opsForValue().increment(key);
        switch (Objects.requireNonNull(increment).intValue()) {
            case 1 ->
                // 第一次调用，初始化过期时间60分钟
                    stringRedisTemplate.expire(key, 60, TimeUnit.MINUTES);
            case 5 ->
                // 达到阈值后，刷新过期时间为5分钟
                    stringRedisTemplate.expire(key, 5, TimeUnit.MINUTES);
            default -> {
            }
        }
        return increment < 6;
    }

    /**
     * 通常情况下，全局异常处理器（如 @RestControllerAdvice）在异常发生时首先被触发。
     * 切面中的异常处理逻辑会在全局异常处理器之后执行。
     * 因为全局异常处理器可以拦截所有控制器中抛出的异常，而切面通常是特定切点上的处理逻辑。
     */
    @AfterThrowing(pointcut = "execution(* org.ashe.security.auth.TestService.aspectException())", throwing = "exception")
    public void handleTestServiceException(Exception exception) {
        // 在这里处理捕获到的异常
        log.error("Exception caught in aspect: " + exception.getMessage());
    }

}