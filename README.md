### 概述
* 此项目是基于SpringBoot 3.1.2和Spring Security 6的登录应用，实现了JWT（JSON Web Token）身份验证和授权。
* 流程：身份验证 --> 授权(颁发token) --> 鉴权(验证token)
* 扩展：手机验证码登录 & 钉钉扫码登录

### 环境准备
```
JDK17
IDEA
Maven
PostgreSQL (建表语句在resources/sql/table.txt)
RabbitMQ
Redis
```

### 启动程序
```
# 设置dev环境启动
Active profiles:dev
```

###### 1、注册用户
```
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
    "mobile": "15283871282",
    "password": "1234",
    "firstname": "Ashe",
    "lastname": "Red"
}
```

###### 2、账号登录
```
POST http://localhost:8080/api/v1/auth/authenticate
Content-Type: application/json

{
    "mobile": "15283871282",
    "password": "1234"
}
```

###### 3、手机验证码登录
```
POST http://localhost:8080/api/v1/auth/code/{{verifyCode}}
```

###### 4、钉钉扫码登录
```
http://127.0.0.1:8080/code.html
```

### 根据令牌访问权限接口
```
# 以上4个接口都会返回token，此后携带token访问需要鉴权的接口
GET http://localhost:8080/api/v1/token/welcome
Authorization: Bearer {{token}}
```