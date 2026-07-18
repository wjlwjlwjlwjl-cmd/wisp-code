FROM openjdk:17
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
WORKDIR /workspace
COPY ./*.jar /workspace/app.jar
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar