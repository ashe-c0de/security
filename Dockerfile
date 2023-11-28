FROM openjdk:17-jdk

WORKDIR /app

# 将当前目录下的jar复制到Docker容器中的指定目录下
COPY ./*.jar /app/security.jar

# 容器启动时执行的命令
CMD ["java", "-jar", "/app/security.jar"]

