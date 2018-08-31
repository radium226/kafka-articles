FROM openjdk:8-jre-alpine

ARG MIRROR="http://apache.crihan.fr/dist"

ARG KAFKA_VERSION="0.11.0.2"
ARG SCALA_VERSION="2.12"

RUN apk add --no-cache "wget" "bash" "gettext" "libintl" "su-exec" && \
    mkdir -p "/opt/kafka" "/tmp/kafka" && \
    wget -q -O - "${MIRROR}/kafka/${KAFKA_VERSION}/kafka_${SCALA_VERSION}-${KAFKA_VERSION}.tgz" | \
      tar -xzC "/opt/kafka" --strip-components=1 && \
    adduser -DH -s "/sbin/nologin" "kafka" && \
    chown -R "kafka" "/opt/kafka" "/tmp/kafka"
ENV PATH "/sbin:/opt/kafka/bin/:${PATH}"

WORKDIR "/opt/kafka"

EXPOSE 9092 9999

COPY ["server.properties.tpl", "/opt/kafka/config/server.properties.tpl"]
COPY ["log4j.properties.tpl", "/opt/kafka/config/log4j.properties.tpl"]
COPY ["entrypoint.sh", "/entrypoint.sh"]

ENTRYPOINT ["/entrypoint.sh"]
CMD ["kafka-server-start.sh", "/opt/kafka/config/server.properties"]