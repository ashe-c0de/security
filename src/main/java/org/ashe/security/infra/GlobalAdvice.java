package org.ashe.security.infra;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局捕获spring mvc抛出的异常
 */
@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalAdvice {

    private final EnvironmentUtils environmentUtils;

    /**
     * we only need catch ServiceException and wrap it to json for view
     * 为了方便使用断言来抛出业务异常，因此同时捕获了IllegalArgumentException (断言抛出的异常统一为IllegalArgumentException)
     */
    @ExceptionHandler({ServiceException.class, IllegalArgumentException.class})
    public ResponseEntity<String> catchServiceException(Exception e) {
        // 记录日志
        // 通知运维
        // 通知开发
        log.error(String.format("======== %s ========", e.getClass().toString()), e);
        return ResponseEntity.status(521).body(e.getMessage());
    }

    @ExceptionHandler(value = EmergencyException.class)
    public ResponseEntity<String> catchException(EmergencyException e) {
        // 记录日志
        log.error(e.getTitle(), e);
        // 通知运维
        // 通知开发
        if (environmentUtils.isDev()) {
            ExceptionAlarm.noticeDeveloper(e.getMessage(), e.getDeveloper(), e.getTitle());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("系统异常，已通知开发人员");
    }

    /**
     * mobile not found or password not match
     */
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<String> catchException(Exception e) {
        // 记录日志
        // 通知运维
        // 通知开发
        log.error(String.format("======== %s ========", e.getClass().toString()), e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("mobile or password is wrong!");
    }

}
