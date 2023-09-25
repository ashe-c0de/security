FROM openjdk:17-jdk

WORKDIR /app

# 将当前目录下的jar复制到Docker容器中的指定目录下
COPY ./*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]

# 指定test环境
ENV SPRING_PROFILES_ACTIVE test
