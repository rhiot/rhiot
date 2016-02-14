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

FROM resin/rpi-raspbian:jessie

MAINTAINER Greg AUTRIC <gautric@redhat.com>

## to build it
## sudo docker build -t ${USER}/rpi-rhiot-${RPI_VERSION}:${RHIOT_VERSION} --build-arg  RHIOT_VERSION=${RHIOT_VERSION} --build-arg  KURA_VERSION=${KURA_VERSION} --build-arg RPI_VERSION=${RPI_VERSION} .
## to run it
## sudo docker run -i -p 80:80 -p 5002:5002 -t ${USER}/rpi-rhiot-${RPI_VERSION}:${RHIOT_VERSION}
## or
## sudo docker run -i -P -t ${USER}/rpi-rhiot -${RPI_VERSION}:${RHIOT_VERSION} 

## Variable ENV
ENV CAMEL_VERSION=${CAMEL_VERSION:-2.16.1}
ENV JAVA_VERSION=${JAVA_VERSION:-7}

ARG RHIOT_VERSION
ENV RHIOT_VERSION=${RHIOT_VERSION:-0.1.4-SNAPSHOT}
ARG KURA_VERSION
ENV KURA_VERSION=${KURA_VERSION:-1.3.0}
ARG RPI_VERSION
ENV RPI_VERSION=${RPI_VERSION:-raspberry-pi}

ENV JAVA_HOME=/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-armhf
ENV KURA_HOME=/opt/eclipse/kura
ENV RHIOT_FOLDER=/opt/rhiot
ENV RHIOT_EMBEDDED_PLUGINS_FOLDER=${RHIOT_FOLDER}/plugins-embedded
ENV RHIOT_BIN_FOLDER=${RHIOT_FOLDER}/bin

LABEL version="${RHIOT_VERSION}"
LABEL project="Rhiot"
LABEL projectURL="http://rhiot.io"
LABEL description="Rhiot docker image"

## Debian/Raspbian package installation
## List comes from http://wiki.eclipse.org/Kura/Raspberry_Pi
RUN apt-get update && \
    apt-get install -y apt-utils unzip ethtool dos2unix telnet bind9 hostapd isc-dhcp-server iw monit wget openjdk-${JAVA_VERSION}-jdk --no-install-recommends  && \
    rm -rf /var/lib/apt/lists/*

## Kura installation
RUN wget https://s3.amazonaws.com/kura_downloads/raspbian/release/${KURA_VERSION}/kura_${KURA_VERSION}_${RPI_VERSION}_installer.deb
RUN dpkg -i kura_${KURA_VERSION}_${RPI_VERSION}_installer.deb
RUN rm kura_${KURA_VERSION}_${RPI_VERSION}_installer.deb

## Hack for debian/jessie
RUN if [ -d $(dirname `find /lib -name libudev.so.1`) ] && [ ! -f $(dirname `find /lib -name libudev.so.1`)/libudev.so.0 ] ; then ln -sf `find /lib -name libudev.so.1` $(dirname `find /lib -name libudev.so.1`)/libudev.so.0; fi

RUN mkdir -p ${RHIOT_EMBEDDED_PLUGINS_FOLDER}
RUN mkdir -p ${RHIOT_BIN_FOLDER}

## Rhiot installation
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
