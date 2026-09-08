FROM eclipse-temurin:21-jre-jammy

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /workspace
LABEL maintainer="wangjiale"

COPY target/*.jar /workspace/app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]