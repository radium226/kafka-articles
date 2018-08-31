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

COPY ["schema-registry.properties.tpl", "/opt/confluence/etc/schema-registry/schema-registry.properties.tpl"]
COPY ["log4j.properties.tpl", "/opt/confluence/etc/schema-registry/log4j.properties.tpl"]
COPY ["entrypoint.sh", "/entrypoint.sh"]

EXPOSE 8082

ENTRYPOINT ["/entrypoint.sh"]
CMD ["/opt/confluence/bin/schema-registry-start", "/opt/confluence/etc/schema-registry/schema-registry.properties"]
