[![Build Status](https://travis-ci.org/grant-burgess/spring-experiments.svg?branch=master)](https://travis-ci.org/grant-burgess/spring-experiments)

# Retry using dead letter queue

Kotlin-based pub/sub model using Spring Cloud Stream Rabbit to explore retry using a dead letter queue

This experiment comprises of two projects
* Publisher service, sends messages to a RabbitMQ exchange every second
* Subscriber service, listens for messages from a particular exchange and fail randomly

The subscriber will randomly fail and requeue a message which will never recover. After 3 attempts the message is moved to a parking lot exchange.

## Prerequisites

- Default Docker machine running

```bash
# (1) Start your default Docker machine
docker-machine start
```

- RabbitMQ running

```bash
# (2) Start RabbitMQ
docker run -d -it --hostname local-rabbit --network host --name local-rabbit rabbitmq:3-management
# http://192.168.99.100:15672 login with guest/guest

# Already created the container?
docker start local-rabbit
```

## Running


```bash
# (1) Start Subscriber service
# Port: random port number
./gradlew :subscriber-service:bootRun

# (2) Start Publisher service
# Port: random port number
./gradlew :publisher-service:bootRun
```


Part 2 - use Docker compose