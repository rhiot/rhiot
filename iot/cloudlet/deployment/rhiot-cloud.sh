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

service docker start

docker stop $(docker ps -q)

docker run -d -p 9000:9000 -e LIVE_RELOAD=false \
  -e GOOGLE_OAUTH_CLIENT_ID=$GOOGLE_OAUTH_CLIENT_ID -e GOOGLE_OAUTH_CLIENT_SECRET=$GOOGLE_OAUTH_CLIENT_SECRET \
  -eGOOGLE_OAUTH_REDIRECT_URI=$GOOGLE_REDIRECT_URI \
  rhiot/cloudlet-console