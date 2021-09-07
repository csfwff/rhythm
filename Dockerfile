FROM docker.io/library/maven:3.8-jdk-8-openj9@sha256:d468fd6bfb0305f59dac8e92d206c4d86c2a90d3faf35435f1dbdbd646870bd9 as MVN_BUILD

WORKDIR /opt/sym/
ADD . /tmp
RUN cd /tmp && mvn package -DskipTests -Pci -q && mv target/symphony/* /opt/sym/ \
&& cp -f /tmp/src/main/resources/docker/* /opt/sym/

FROM openjdk:8-alpine
LABEL maintainer="Liang Ding<845765@qq.com>"

WORKDIR /opt/sym/
COPY --from=MVN_BUILD /opt/sym/ /opt/sym/
RUN apk add --no-cache ca-certificates tzdata ttf-dejavu

ENV TZ=Asia/Shanghai
EXPOSE 8080

ENTRYPOINT [ "java", "-cp", "lib/*:.", "org.b3log.symphony.Server" ]
