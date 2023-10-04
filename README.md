# Security
* 此项目是基于Spring Security 3.1.2的登录应用，实现了JWT（JSON Web Token）身份验证和授权。
* 流程：身份验证 --> 授权(颁发token) --> 鉴权(验证token)
* 扩展：手机验证码登录 & 钉钉扫码登录

### Features
* 注册 & 携带token访问
* 扩展手机验证码登录 & 钉钉扫码登录
* 自定义接口请求权限
* 自定义接口请求频率
* 钉钉机器人实时报警webhook

### Technologies
* Spring Boot 3.1.2
* Spring Security 3.1.2
* JSON Web Token (JWT)
* Redis
* RabbitMQ

### Getting Started
you will need to have the following installed on your local machine:
* JDK17
* IDEA
* PostgreSQL (建表语句在resources/sql/table.txt)
* RabbitMQ
* Redis
* Active profiles:dev (设置dev环境启动)
* build and run the project

-> The application will be available at http://localhost:8080