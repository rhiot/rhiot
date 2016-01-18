#
# Licensed to the Rhiot under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

KURA_VERSION=1.3.0
RPI_VERSION=raspberry-pi
DOCKER_IMAGE_NAME=rhiot/kura-$(RPI_VERSION)
DOCKER_IMAGE_TAGNAME=$(DOCKER_IMAGE_NAME):$(KURA_VERSION)
ARCH=$(arch)

RPI_VERSION_LIST=raspberry-pi raspberry-pi-nn raspberry-pi-bplus raspberry-pi-bplus-nn raspberry-pi-2 raspberry-pi-2-nn

ifneq (`arch`,arm)
       RPI_OS=debian
endif

default: build

build: 
	docker build -t $(DOCKER_IMAGE_TAGNAME) --build-arg  KURA_VERSION=${KURA_VERSION} --build-arg RPI_VERSION=${RPI_VERSION} .
	docker tag -f $(DOCKER_IMAGE_TAGNAME) $(DOCKER_IMAGE_NAME):latest

push:
	docker push $(DOCKER_IMAGE_NAME)

all:
	for version in ${RPI_VERSION_LIST} ; do \
		make build RPI_VERSION=$$version ; \
	done

pushall: buildall
	for version in ${RPI_VERSION_LIST} ; do \
		make push RPI_VERSION=$$version ; \
	done
