#  Licensed to the Camel Labs under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

#!/usr/bin/env bash

if [ -z "$1" ]
then
  export DEPLOY_CLOUDLETS=true
  export DEPLOY_UI=true
else
  export "${1}"=true
fi

if [ -z "$CLOUD_SSH_ROOT" ]
then
  echo '$CLOUD_SSH_ROOT not set.'
  exit 1
fi

if [ ! -z "$DEPLOY_CLOUDLETS" ]
then
  echo Deploying cloudlets UI.
  ssh $CLOUD_SSH_ROOT 'mkdir -p /var/camel-iot-labs/cloudlets'
  scp ../geofencing/default/target/camel-labs-iot-cloudlet-geofencing-0.1.1-SNAPSHOT.jar $CLOUD_SSH_ROOT:/var/camel-iot-labs/cloudlets
fi

if [ ! -z "$DEPLOY_UI" ]
then
  echo Deploying cloudlets UI.
  ssh $CLOUD_SSH_ROOT 'mkdir -p /var/camel-iot-labs/cloudlets-ui'
  scp ././../../../cloudlet-ui/geofencing-ui/target/camel-labs-iot-cloudlet-ui-geofencing-0.1.1-SNAPSHOT-plugin.zip $CLOUD_SSH_ROOT:/var/camel-iot-labs/cloudlets-ui
fi