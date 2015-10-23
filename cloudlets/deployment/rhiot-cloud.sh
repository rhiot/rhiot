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

### Docker server setup

echo Checking Docker setup...

if ! type "docker" > /dev/null; then
  echo 'Docker not found - installing...'
  wget -qO- https://get.docker.com/ | sh
fi

REQUIRED_DOCKER_VERSION=1.8.2
DOCKER_VERSION=`docker version --format '{{.Server.Version}}'`
if [ "$DOCKER_VERSION" \< "$REQUIRED_DOCKER_VERSION" ]; then
  echo "Docker ${REQUIRED_DOCKER_VERSION} is required to run Rhiot Cloud. Version ${DOCKER_VERSION} found - upgrading..."
  wget -qO- https://get.docker.com/ | sh
else
  echo "Docker v${DOCKER_VERSION} found. No need to upgrade."
fi

echo Docker is properly installed.

### Docker server setup ends

service docker start

docker stop $(docker ps -q)

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

### Geofencing Cloudlet
docker rm cloudlet-geofencing
docker pull rhiot/cloudlet-geofencing
docker run -d --name cloudlet-geofencing --link mongodb:mongodb -p 15001:15001 rhiot/cloudlet-geofencing

### Webcam Cloudlet
docker rm cloudlet-webcam
docker pull rhiot/cloudlet-webcam
docker run -d --name cloudlet-webcam --link mongodb:mongodb -p 15002:15002 rhiot/cloudlet-webcam

if [ -z "$HTTP_PORT" ]; then
    echo 'HTTP port not set, running Cloudlet Console using the default development port 9000.'
    HTTP_PORT=9000
fi
if [ -z "$GOOGLE_OAUTH_REDIRECT_URI" ]; then
    GOOGLE_OAUTH_REDIRECT_URI=http://localhost:${HTTP_PORT}
fi
docker pull rhiot/cloudlet-console
docker run -d -p ${HTTP_PORT}:${HTTP_PORT} -e HTTP_PORT=${HTTP_PORT} -e LIVE_RELOAD=false \
  -e GOOGLE_OAUTH_CLIENT_ID=$GOOGLE_OAUTH_CLIENT_ID -e GOOGLE_OAUTH_CLIENT_SECRET=$GOOGLE_OAUTH_CLIENT_SECRET \
  -e GOOGLE_OAUTH_REDIRECT_URI=$GOOGLE_OAUTH_REDIRECT_URI \
  rhiot/cloudlet-console