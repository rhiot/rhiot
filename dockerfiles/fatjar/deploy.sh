#!/bin/sh

docker build -t rhiot/fatjar:0.1.1-SNAPSHOT .
docker push rhiot/fatjar:0.1.1-SNAPSHOT