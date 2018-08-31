#!/bin/bash

set -e

IP="$( ip -o -4 addr list eth0 | awk '{print $4}' | cut -d/ -f1 )"
GUEST_HOSTNAME="$( hostname )"
HOST_HOSTNAME="$( ip route | awk '/default/ { print $3 }' )"

if [[ -z "${LOCAL}" ]]; then
    HOST_HOSTNAME="${GUEST_HOSTNAME}"
fi

ADVERTISED_LISTENERS="${ADVERTISED_LISTENERS:-PLAINTEXT://${HOST_HOSTNAME}:9092}" \
LISTENERS="${LISTENERS:-PLAINTEXT://${GUEST_HOSTNAME}:9092}" \
AUTO_CREATE_TOPICS="${AUTO_CREATE_TOPICS:-false}" \
    envsubst <"/opt/confluence/etc/kafka/server.properties.tpl" >"/opt/confluence/etc/kafka/server.properties"

LOG_LEVEL="${LOG_LEVEL:-WARN}" \
    envsubst <"/opt/confluence/etc/kafka/log4j.properties.tpl" >"/opt/confluence/etc/kafka/log4j.properties"

KAFKA_JMX_OPTS="-Dcom.sun.management.jmxremote.port=9969 -Djava.net.preferIPv4Stack=true -Dcom.sun.management.jmxremote=true -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=${HOSTNAME}" \
    exec gosu "confluence" "$@"
