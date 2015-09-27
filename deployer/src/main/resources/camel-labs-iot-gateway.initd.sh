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

#!/bin/bash
# /etc/init.d/camel-labs-iot-gateway

### BEGIN INIT INFO
# Provides:          camel-labs-iot-gateway
# Required-Start:    $syslog
# Required-Stop:     $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Camel Labs IoT Gateway
# Description:       Camel Labs IoT Gateway
### END INIT INFO


case "$1" in
    start)
        echo "Starting Camel Labs IoT Gateway..."
        mkdir -p /var/camel-labs-iot-gateway
        echo "Loading gateway configuration from /etc/default/camel-labs-iot-gateway file..."
        . /etc/default/camel-labs-iot-gateway
        java -jar /var/camel-labs-iot-gateway/camel-labs-iot-gateway-*.jar >/var/camel-labs-iot-gateway/camel-labs-iot-gateway.log 2>&1 &
        echo $! > /var/camel-labs-iot-gateway/pid
        echo "Camel Labs IoT Gateway started."
        ;;
    stop)
        echo "Stopping Camel Labs IoT Gateway"
        kill -9 `cat /var/camel-labs-iot-gateway/pid`
        ;;
    *)
        echo "Usage: /etc/init.d/camel-labs-iot-gateway start|stop"
        exit 1
        ;;
esac

exit 0