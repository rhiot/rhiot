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

#!/bin/sh

export KURA_HTTP_PORT=${KURA_HTTP_PORT:-80}
export RHIOT_PLUGIN_FOLDER=${RHIOT_PLUGIN_FOLDER:-/opt/rhiot/plugins}

cd /opt/eclipse/kura/kura

#sed -i "" -e "s/.*org.osgi.service.http.port.*=.*\$/org.osgi.service.http.port=${KURA_HTTP_PORT}/" "config.ini"

if [ -d ${RHIOT_PLUGIN_FOLDER} ]; then
  for i in `ls ${RHIOT_PLUGIN_FOLDER} | egrep "*.jar"`; do
    cat config.ini | awk '/^osgi.bundles=/ { print $0 ", reference\\:file\\:'"${RHIOT_PLUGIN_FOLDER}/${i}"'@5\\:start" }   !/osgi.bundles=/ { print $0  }' > config.ini.tmp
    mv config.ini.tmp config.ini
    # FIX ME sed -i "" -e "s/osgi.bundles=.*/&1, reference\\\:file\\\:${i}@5\\\:start/" "config.ini"
  done
fi

cd - > /dev/null

/opt/eclipse/kura/bin/start_kura.sh
