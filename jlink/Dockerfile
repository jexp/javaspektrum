# Multi-stage Docker build
FROM openjdk:17.0.2 as runtime-build

# 1. Mittels Jlink eigenes JDK bauen
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /custom-jdk

# Stage 2. Custom JDK in Debian JVM installieren
FROM debian:buster-slim
# alpine:latest
ENV LANG en_US.UTF-8
ENV JAVA_HOME /usr/lib/jvm/custom-jdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=runtime-build /custom-jdk $JAVA_HOME

# Continue with your application deployment
RUN mkdir /opt/app
COPY hello.jar /opt/app
CMD ["java", "-jar", "/opt/app/hello.jar"]
