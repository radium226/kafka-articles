FROM openjdk:latest

ARG PACKAGE_URL="https://packages.confluent.io/archive/5.0/confluent-oss-5.0.0-2.11.tar.gz"

RUN apt-get update && \
    apt-get -y install "gosu" "gettext-base"

RUN mkdir -p "/opt/confluence" "/tmp/confluence" && \
    wget -q -O - "${PACKAGE_URL}" | \
      tar -xzC "/opt/confluence" --strip-components=1 && \
    adduser \
      --no-create-home \
      --disabled-password \
      --shell "/sbin/nologin" \
      --gecos "" \
      "confluence" && \
    chown -R "confluence" "/opt/confluence" "/tmp/confluence"
ENV PATH "/sbin:/opt/confluence/bin/:${PATH}"

WORKDIR "/opt/confluence"

EXPOSE 8082

COPY ["server.properties.tpl", "/opt/confluence/etc/kafka/server.properties.tpl"]
COPY ["log4j.properties.tpl", "/opt/confluence/etc/kafka/log4j.properties.tpl"]
COPY ["entrypoint.sh", "/entrypoint.sh"]

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/opt/confluence/bin/kafka-server-start", "/opt/confluence/etc/kafka/server.properties"]
