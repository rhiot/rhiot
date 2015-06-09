 #
 # Licensed to the Camel Labs under one or more
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

#!/usr/bin/env bash

CAMEL_GATEWAY_HOME=/var/camel-labs-iot-gateway

# Prepare gateway home directory
mkdir -p CAMEL_GATEWAY_HOME

# Remove existing Gateway artifacts
echo 'This is dummy file used by the installation script.' > ${CAMEL_GATEWAY_HOME}/camel-labs-iot-gateway-TMP.jar
rm ${CAMEL_GATEWAY_HOME}/camel-labs-iot-gateway-*.jar

# Download and install the latest gateway
wget http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.github.camellabs&a=camel-labs-iot-gateway&v=LATEST --directory-prefix=/tmp
mv /tmp/camel-labs-iot-gateway-*.jar ${CAMEL_GATEWAY_HOME}/

# Download and install the init.d script
sudo wget https://raw.githubusercontent.com/camel-labs/camel-labs/master/iot/initd/raspbian/camel-labs-iot-gateway.sh -O /etc/init.d/camel-labs-iot-gateway
sudo wget https://raw.githubusercontent.com/camel-labs/camel-labs/master/iot/initd/raspbian/camel-labs-iot-gateway-config.sh -O /etc/default/camel-labs-iot-gateway
sudo chmod +x /etc/init.d/camel-labs-iot-gateway
sudo update-rc.d camel-labs-iot-gateway defaults

# Start the installed gateway service
sudo /etc/init.d/camel-labs-iot-gateway start