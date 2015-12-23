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

ARCH=$(arch)
RPI_OS=hypriot/rpi-java
RPI_DOCKER_IMAGE_NAME=makefilerpi-kura

ifneq (`arch`,arm)
	RPI_OS=debian
endif

all : searchreplace
	@echo "Docker building ..."
	docker build -t ${RPI_DOCKER_IMAGE_NAME} .

searchreplace : Dockerfile
	@echo "Create a specific Dockerfile depending on "`arch`
	sed -i "" -e "s/FROM.*/FROM ${RPI_OS}/" "Dockerfile"

run : all
	docker run -d -p 80:80 -t ${RPI_DOCKER_IMAGE_NAME}
