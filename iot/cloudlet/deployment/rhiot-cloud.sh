#  Licensed to the Camel Labs under one or more
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

REQUIRED_DOCKER_VERSION=1.7.1
DOCKER_VERSION=`docker version | grep 'Server version' | cut -d ' ' -f 3`
if [ "$DOCKER_VERSION" \< "$REQUIRED_DOCKER_VERSION" ]; then
  echo "Docker ${REQUIRED_DOCKER_VERSION} is required to run Rhiot Cloud. Version ${DOCKER_VERSION} found - upgrading..."
  wget -qO- https://get.docker.com/ | sh
fi

echo Docker has been properly installed.

### Docker server setup ends

service docker start

docker stop $(docker ps -q)

docker rm mongodb
docker run -d --name mongodb -p 27017:27017 mongo

docker run -d -p 9000:9000 -e LIVE_RELOAD=false \
  -e GOOGLE_OAUTH_CLIENT_ID=$GOOGLE_OAUTH_CLIENT_ID -e GOOGLE_OAUTH_CLIENT_SECRET=$GOOGLE_OAUTH_CLIENT_SECRET \
  -e GOOGLE_OAUTH_REDIRECT_URI=$GOOGLE_OAUTH_REDIRECT_URI \
  rhiot/cloudlet-console