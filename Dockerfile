FROM openjdk:17-jdk

WORKDIR /app

COPY ./*.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]

# 指定test环境
ENV SPRING_PROFILES_ACTIVE test
