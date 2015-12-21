#!/bin/bash
# /etc/init.d/rhiot-gateway

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

### BEGIN INIT INFO
# Provides:          rhiot-gateway
# Required-Start:    $syslog
# Required-Stop:     $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Rhiot field gateway
# Description:       Rhiot field gateway
### END INIT INFO

GATEWAY_HOME=/var/rhiot-gateway

case "$1" in
    start)
        echo "Starting Rhiot field gateway..."
        mkdir -p /var/rhiot
        echo "Loading gateway configuration from /etc/default/rhiot-gateway file..."
        . /etc/default/rhiot-gateway
        java -jar ${GATEWAY_HOME}/rhiot-gateway-*.jar >"${GATEWAY_HOME}/rhiot-gateway.log" 2>&1 &
        echo $! > "${GATEWAY_HOME}/pid"
        echo "Rhiot field gateway started."
        ;;
    stop)
        echo "Stopping Rhiot field gateway"
        kill -9 `cat ${GATEWAY_HOME}/pid`
        ;;
    *)
        echo "Usage: /etc/init.d/rhiot-gateway start|stop"
        exit 1
        ;;
esac

exit 0