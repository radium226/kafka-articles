# Copyright 2014 Confluent Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# The address the socket server listens on.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=http://0.0.0.0:8081

# Zookeeper connection string for the Zookeeper cluster used by your Kafka cluster
# (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
kafkastore.connection.url=${KAFKA_ZOOKEEPER_CONNECT}

# Alternatively, Schema Registry can now operate without Zookeeper, handling all coordination via
# Kafka brokers. Use this setting to specify the bootstrap servers for your Kafka cluster and it
# will be used both for selecting the master schema registry instance and for storing the data for
# registered schemas.
# (Note that you cannot mix the two modes; use this mode only on new deployments or by shutting down
# all instances, switching to the new configuration, and then starting the schema registry
# instances again.)
kafkastore.bootstrap.servers=PLAINTEXT://${KAFKA_BOOTSTRAP_SERVERS}

# The name of the topic to store schemas in
kafkastore.topic=_schemas

# If true, API requests that fail will include extra debugging information, including stack traces
debug=false
