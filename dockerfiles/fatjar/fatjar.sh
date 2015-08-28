#!/bin/sh

# Allows to set -Xmx Java option.
if [ ! -z ${XMX} ]; then
    XMX="-Xmx=${XMX}"
fi

java ${XMX} -jar /jars/* "$@"