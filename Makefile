.PHONY: clean
clean:
	cd "./examples" && \
	sbt test:clean
	test -f "./docker-images/examples/examples-tests.jar" && \
	  rm "./docker-images/examples/examples-tests.jar" || true

./docker-images/examples/examples-tests.jar: package-jar

.PHONY: docker-images
docker-images: zookeeper-docker-image kafka-docker-image examples-docker-image schema-registry-docker-image

.PHONY: %-docker-image
%-docker-image:
	cd "docker-images" && \
	docker-compose build "$(subst -docker-image,,$@)"

.PHONY: examples-docker-image
examples-docker-image: ./docker-images/examples/examples-tests.jar
	cd "docker-images" && \
	  docker-compose \
	    build \
			  "examples"

.PHONY: build
build: docker-images

.PHONY: docker-compose-up
docker-compose-up: docker-images
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		up \
			--detach \
			--force-recreate
	sleep 5 #FIXME Add proper smoke tests

.PHONY: docker-compose-down
docker-compose-down:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		down \
			--timeout 1

.PHONY: docker-compose-kill
docker-compose-kill:
	cd "./docker-images" && \
	docker-compose -p "akka-kafka-confluent-examples" kill

tail-logs:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		logs \
			--follow

.PHONY: examples
examples: docker-compose-up
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"examples" \
			"/entrypoint.sh" "tests"

.PHONY: package-jar
package-jar:
	cd "./examples" && \
	sbt "set Test / assembly / test := {}" test:assembly
	cp \
	  "$$( find "./examples/target" -name "*-tests.jar" )" \
	  "./docker-images/examples/examples-tests.jar"

.PHONY: docker-compose-exec-examples
docker-compose-exec-examples:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"examples" \
			bash -i

.PHONY: feedback-loop
feedback-loop:
	# We package the JAR
	cd "./examples" && \
	sbt "set Test / assembly / test := {}" test:assembly

	# We copy it next to the Dockerfile
	cp \
	  "$$( find "./examples/target" -name "*-tests.jar" )" \
	  "./docker-images/examples/examples-tests.jar"

	# We stop all services
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		down \
			-t 1

	# We rebuild the examples Docker image
	cd "docker-images" && \
	  docker-compose \
	    build \
			  "examples"

	# We start all services
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		up \
			--force-recreate \
			--detach

	# And we finally run the tests
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"examples" \
			"/entrypoint.sh" "tests"

.PHONY: tests
tests:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"examples" \
			"/entrypoint.sh" "tests"

.PHONY: list-subjects
list-subjects:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"schema-registry" \
			curl -X "GET" "http://localhost:8081/subjects/" | jq

consume-topic-avro:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"schema-registry" \
			kafka-avro-console-consumer \
				--bootstrap-server "kafka:9092" \
				--topic "$(TOPIC)" --from-beginning

consume-topic:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"kafka" \
			kafka-console-consumer \
				--bootstrap-server "kafka:9092" \
				--topic "$(TOPIC)" --from-beginning

bash:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"$(SERVICE)" \
			bash -i

list-topics:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"kafka" \
			kafka-topics \
				--zookeeper "zookeeper:2181" \
				--list

describe-topic:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"kafka" \
			kafka-topics \
				--zookeeper "zookeeper:2181" \
				--topic "$(TOPIC)" \
				--describe

describe-consumer-group:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"kafka" \
			kafka-consumer-groups \
				--bootstrap-server "kafka:9092" \
				--group "$(GROUP)" \
				--describe \
				--verbose

reset-offset-to-earliest:
	cd "./docker-images" && \
	docker-compose \
		-p "akka-kafka-confluent-examples" \
		exec \
			"kafka" \
			kafka-consumer-groups \
				--bootstrap-server "kafka:9092" \
				--group "$(GROUP)" \
				--reset-offsets \
				--to-earliest \
				--topic "$(TOPIC)" \
				--execute

#logs:
#	cd "./docker-images" && \
#	docker-compose -p "akka-kafka-confluent-examples" logs
