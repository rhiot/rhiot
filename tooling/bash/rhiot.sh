#!/usr/bin/env bash

### Docker server setup

echo Checking Docker setup...

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



docker run -v ~/rhiot/maven/repository:/root/.m2/repository --net=host -it rhiot/deploy-gateway