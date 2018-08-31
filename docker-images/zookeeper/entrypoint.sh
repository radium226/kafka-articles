#!/bin/bash

set -e

LOG_LEVEL="${LOG_LEVEL:-WARN}" \
    envsubst <"/opt/zookeeper/conf/log4j.properties.tpl" >"/opt/zookeeper/conf/log4j.properties"

su-exec "zookeeper" "$@"