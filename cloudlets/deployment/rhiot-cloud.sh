#  Licensed to the Rhiot under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

#!/usr/bin/env bash

### General configuration

if [ -z "${RHIOT_VERSION}" ]; then
    RHIOT_VERSION=0.1.3
fi

REQUIRED_DOCKER_VERSION=1.8.2

### Docker

echo Checking Docker setup...

if ! type "docker" > /dev/null; then
  echo 'Docker not found - installing...'
  wget -qO- https://get.docker.com/ | sh
fi

DOCKER_VERSION=`docker version --format '{{.Server.Version}}'`
if [ "$DOCKER_VERSION" \< "$REQUIRED_DOCKER_VERSION" ]; then
  echo "Docker ${REQUIRED_DOCKER_VERSION} is required to run Rhiot Cloud. Version ${DOCKER_VERSION} found - upgrading..."
  wget -qO- https://get.docker.com/ | sh
else
  echo "Docker v${DOCKER_VERSION} found. No need to upgrade."
fi

echo Docker is properly installed.

service docker start

docker stop $(docker ps -q)

### MongoDB

MONGODB_DATA_VOLUMES=`docker ps -a | grep mongodb_data | wc -l`
if [ "$MONGODB_DATA_VOLUMES" \< 1 ]; then
    echo "MongoDB data volume doesn't exist. Creating..."
    docker run -v /data/db --name mongodb_data busybox true
fi
docker rm mongodb
docker run -d --volumes-from mongodb_data --name mongodb -p 27017:27017 mongo

### Device Management Cloudlet

docker rm cloudlet-device
docker pull rhiot/cloudlet-device
if [ ! -z "$lwm2m_port" ]; then
    lwm2m_port="-p ${lwm2m_port}:${lwm2m_port}"
else
    echo "Using default LWM2M port (5683)."
    lwm2m_port="-p 5683:5683"
fi
docker run --name cloudlet-device -d --link mongodb:mongodb -p 15000:15000 ${lwm2m_port} rhiot/cloudlet-device

### Data stream node

docker rm datastream-node
docker pull rhiot/datastream-node
docker run -d --name datastream-node --link mongodb:mongodb -p 8080:8080 -p 5672:5672 rhiot/datastream-node

### Spark standalone cluster

docker pull rhiot/spark-standalone:${RHIOT_VERSION}

docker rm spark_master
docker run -d --name spark_master -p 8081:8080 -P -t rhiot/spark-standalone:${RHIOT_VERSION} /start-master.sh "$@"
SPARK_MASTER_SERVICE_HOST=`docker inspect spark_master | grep IPAddress\": | cut -d '"' -f 4`

docker rm spark_worker
docker run -d --name spark_worker -e SPARK_MASTER_SERVICE_HOST=${SPARK_MASTER_SERVICE_HOST} -v /tmp/jobs:/tmp/jobs --link spark_master:spark_master -P \
  -t rhiot/spark-standalone:${RHIOT_VERSION} /start-worker.sh
