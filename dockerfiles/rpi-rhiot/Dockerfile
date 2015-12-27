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

FROM rpi-kura

MAINTAINER Greg AUTRIC <gautric@redhat.com>

## to build it
## sudo docker build -t rpi-rhiot .
## to run it
## sudo docker run -t rpi-rhiot

## Variable ENV
ENV CAMEL_VERSION=2.16.1
ENV RHIOT_VERSION=0.1.3

ENV RHIOT_FOLDER=/opt/rhiot
ENV RHIOT_EMBEDDED_PLUGINS_FOLDER=${RHIOT_FOLDER}/plugins-embedded
ENV RHIOT_BIN_FOLDER=${RHIOT_FOLDER}/bin

LABEL version="${RHIOT_VERSION}"
LABEL project="Rhiot"
LABEL projectURL="http://rhiot.io"
LABEL description="Rhiot docker image"

RUN mkdir -p ${RHIOT_EMBEDDED_PLUGINS_FOLDER}
RUN mkdir -p ${RHIOT_BIN_FOLDER}

## Kura installation
RUN wget -P ${RHIOT_EMBEDDED_PLUGINS_FOLDER} https://repo1.maven.org/maven2/org/apache/camel/camel-core/${CAMEL_VERSION}/camel-core-${CAMEL_VERSION}.jar
RUN wget -P ${RHIOT_EMBEDDED_PLUGINS_FOLDER} https://repo1.maven.org/maven2/org/apache/camel/camel-core-osgi/${CAMEL_VERSION}/camel-core-osgi-${CAMEL_VERSION}.jar
RUN wget -P ${RHIOT_EMBEDDED_PLUGINS_FOLDER} https://repo1.maven.org/maven2/org/apache/camel/camel-kura/${CAMEL_VERSION}/camel-kura-${CAMEL_VERSION}.jar
RUN wget -P ${RHIOT_EMBEDDED_PLUGINS_FOLDER} https://repo1.maven.org/maven2/io/rhiot/camel-kura/${RHIOT_VERSION}/camel-kura-${RHIOT_VERSION}.jar

## Add file
ADD ./config.ini.sh       ${RHIOT_BIN_FOLDER}/
ADD ./start_kura_rhiot.sh ${RHIOT_BIN_FOLDER}/
ADD ./log4j.properties    /opt/eclipse/kura/kura

## Custom Kura installation
RUN chmod 755 ${RHIOT_BIN_FOLDER}/*.sh
RUN ${RHIOT_BIN_FOLDER}/config.ini.sh

## Web and telnet
EXPOSE 80
EXPOSE 5002

## Main start
CMD ${RHIOT_BIN_FOLDER}/start_kura_rhiot.sh
