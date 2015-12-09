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

if ! type "docker" > /dev/null; then
  echo 'Docker not found - installing...'
  wget -qO- https://get.docker.com/ | sh
else
  REQUIRED_DOCKER_VERSION=1.8.2
  DOCKER_VERSION=`docker version --format '{{.Server.Version}}'`
  if [ "$DOCKER_VERSION" \< "$REQUIRED_DOCKER_VERSION" ]; then
    echo "Docker ${REQUIRED_DOCKER_VERSION} is required to run Rhiot. Version ${DOCKER_VERSION} found - upgrading..."
    wget -qO- https://get.docker.com/ | sh
  fi
fi

### Rhiot version setup

if [ -z "$RHIOT_VERSION" ]; then
    # RHIOT_VERSION=0.1.3
    RHIOT_VERSION=0.1.3-SNAPSHOT # Remove this line and use the locked version above after 0.1.3 release
fi

### Command execution

docker run -v ~/.rhiot/maven/repository:/root/.m2/repository \
           -v ~/.rhiot/downloads:/root/.rhiot/downloads \
           -v /dev:/root/hostdev -e devices_directory=/root/hostdev \
           --net=host -p 2000:2000 -it rhiot/cmd:${RHIOT_VERSION} "$@"