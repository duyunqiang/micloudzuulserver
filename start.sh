#!/bin/bash

## MI Cloud Zuul Server

export SERVER_PORT=8765
export MGMT_SERVER_PORT=8766
export EUREKA_SERVER_URI=http://localhost:8761

if [ "$1" == "debug" ]; then
    mvn spring-boot:run -Drun.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5009" --debug
else
    mvn spring-boot:run $@
fi
