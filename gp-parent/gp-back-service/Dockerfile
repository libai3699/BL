FROM amazoncorretto:latest

MAINTAINER gp

# 创建目录
RUN mkdir -p /gp/tmpdir
RUN mkdir -p /home/appuser

EXPOSE 13002


WORKDIR /gp
COPY ./target/gp-back-service-encrypted.jar ./

RUN yum -y install shadow-utils
RUN groupadd -r appgroup && useradd -r -g appgroup appuser && chown -R appuser:appgroup /home/appuser /gp && chmod -R 777 /home/appuser && chmod 777 /gp/gp-back-service-encrypted.jar

CMD java -Djava.io.tmpdir=/gp/tmpdir -javaagent:gp-back-service-encrypted.jar  -jar -XX:-DisableAttachMechanism -Dfile.encoding=UTF-8 -Duser.timezone=GMT+08 gp-back-service-encrypted.jar
