FROM amazoncorretto:latest

MAINTAINER gp

# 创建目录
RUN mkdir -p /gp/tmpdir
RUN mkdir -p /home/appuser

EXPOSE 13005
EXPOSE 9999


WORKDIR /gp
COPY ./target/gp-xxl-encrypted.jar ./

RUN yum -y install shadow-utils
RUN groupadd -r appgroup && useradd -r -g appgroup appuser && chown -R appuser:appgroup /home/appuser /gp && chmod -R 777 /home/appuser && chmod 777 /gp/gp-xxl-encrypted.jar

CMD java -Djava.io.tmpdir=/gp/tmpdir -javaagent:gp-xxl-encrypted.jar  -jar -XX:-DisableAttachMechanism -Dfile.encoding=UTF-8 -Duser.timezone=GMT+08 gp-xxl-encrypted.jar
