#!/bin/bash

## MI Cloud Zuul Server

export EUREKA_SERVER_USER=user
export EUREKA_SERVER_PASS=mindsignited
export CONFIG_SERVER_USER=user
export CONFIG_SERVER_PASS=mindsignited

if [ "$1" == "debug" ]; then
    mvn spring-boot:run -Drun.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5007"
else
    mvn spring-boot:run
fi
