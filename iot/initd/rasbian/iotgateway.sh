#!/bin/bash
# /etc/init.d/iotgateway

### BEGIN INIT INFO
# Provides:          iotgateway
# Required-Start:    $syslog
# Required-Stop:     $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: Camel Labs IoT Gateway
# Description:       Camel Labs IoT Gateway
### END INIT INFO


case "$1" in
    start)
        echo "Starting Camel Labs IoT Gateway"
        mkdir -p /var/iotgateway
        java -jar /home/pi/camel-*.jar &
        echo $! > /var/iotgateway/pid
        ;;
    stop)
        echo "Stopping Camel Labs IoT Gateway"
        kill `cat /var/iotgateway/pid`
        ;;
    *)
        echo "Usage: /etc/init.d/iotgateway start|stop"
        exit 1
        ;;
esac

exit 0