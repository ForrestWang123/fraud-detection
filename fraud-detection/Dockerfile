# # ARG app
FROM anolis-registry.cn-zhangjiakou.cr.aliyuncs.com/openanolis/openjdk:17-8.6

ENV TZ=Asia/Shanghai
ENV LANG UTF-8
# 修改时区
RUN ln -sf /usr/share/zoneinfo/%TZ /etc/localtime

ARG app
ENV env=dev
RUN mkdir -p /app/{lib,tmp,logs}
WORKDIR /app
COPY ./target/fraud-detection-1.0-SNAPSHOT.jar /app

ENTRYPOINT ["/bin/sh", "-c", "set -e && java -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -jar fraud-detection-1.0-SNAPSHOT.jar"]
