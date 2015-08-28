#!/bin/sh

# Allows to set -Xmx Java option.
if [ ! -z ${XMX} ]; then
    XMX="-Xmx${XMX}"
fi

echo "Executing command: java ${XMX} -jar /jars/* $@"
java ${XMX} -jar /jars/* "$@"
