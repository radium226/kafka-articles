FROM openjdk:8-jre-alpine

ARG MIRROR=http://apache.mirrors.pair.com
ARG ZOOKEEPER_VERSION=3.4.10

RUN apk add --no-cache "wget" "bash" "gettext" "libintl" "su-exec" && \
    mkdir -p "/opt/zookeeper" "/tmp/zookeeper" && \
    wget -q -O - "${MIRROR}/zookeeper/zookeeper-${ZOOKEEPER_VERSION}/zookeeper-${ZOOKEEPER_VERSION}.tar.gz" | \
      tar -xzC "/opt/zookeeper" --strip-components=1 && \
    cp "/opt/zookeeper/conf/zoo_sample.cfg" "/opt/zookeeper/conf/zoo.cfg" && \
    adduser -DH -s "/sbin/nologin" "zookeeper" && \
    chown -R "zookeeper:" "/opt/zookeeper" "/tmp/zookeeper"

WORKDIR "/opt/zookeeper"

EXPOSE 2181 2888 3888

COPY ["log4j.properties.tpl", "/opt/zookeeper/conf/log4j.properties.tpl"]
COPY ["entrypoint.sh", "/entrypoint.sh"]

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/opt/zookeeper/bin/zkServer.sh", "start-foreground"]
