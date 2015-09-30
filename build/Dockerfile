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

FROM centos

MAINTAINER Greg Autric <gautric@redhat.com> (@gautric_io)

# From official Dockerfile
# Assemble the full dev environment. This is slow the first time.
# docker build -t rhiot build
#
# # Mount your source in an interactive container for quick testing:
# docker run -v `pwd`:/rhiot --privileged -i -t rhiot
# docker run -v `pwd`:/rhiot --privileged -t rhiot /bin/bash -c "cd /rhiot; mvn clean package -Dmaven.test.skip=true"

RUN yum -y install java-1.8.0-openjdk-devel git maven

ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk
ENV RHIOT_SRC_DIR=/rhiot

CMD cd $RHIOT_SRC_DIR; mvn clean package
