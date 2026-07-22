FROM eclipse-temurin:21-jdk-jammy
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /workspace
COPY target/*.jar /workspace/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar