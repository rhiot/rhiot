#!/usr/bin/env bash

docker run -v ~/rhiot/maven/repository:/root/.m2/repository --net=host -it rhiot/deploy-gateway