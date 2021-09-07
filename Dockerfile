FROM docker.io/library/maven:3.8-jdk-8-openj9@sha256:ac3d42a927ee8bef1dcff45afc7edcf74b3b48badeb9cda106948c026fb93501 as MVN_BUILD

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
