package org.ashe.security.domain;

public class RedisKey {

    private RedisKey(){}

    /**
     * 项目名
     */
    private static final String PROJECT = "security";
    /**
     * 短信验证码
     */
    public static final String SMS_VERIFY_CODE = "sms_verify_code";
    /**
     * IP请求次数
     */
    public static final String REQUEST_COUNT = "ip_request_count";


    /**
     * 获取redis_key
     * 格式 --> 项目名:业务名:业务值
     * @param business      业务名
     * @param value         业务值
     */
    public static String getKey(String business, String value) {
        // 特殊处理本机IP存在redis中的key值
        if ("0:0:0:0:0:0:0:1".equals(value)) {
            value = "localhost";
        }
        return String.format("%s:%s:%s", PROJECT, business, value);
    }

}