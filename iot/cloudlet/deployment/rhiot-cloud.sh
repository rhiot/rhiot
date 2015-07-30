#!/usr/bin/env bash

service docker start

docker stop $(docker ps -q)

docker run -d -p 9000:9000 -e LIVE_RELOAD=false \
  -e GOOGLE_OAUTH_CLIENT_ID=$GOOGLE_OAUTH_CLIENT_ID -e GOOGLE_OAUTH_CLIENT_SECRET=$GOOGLE_OAUTH_CLIENT_SECRET \
  -eGOOGLE_OAUTH_REDIRECT_URI=$GOOGLE_REDIRECT_URI \
  rhiot/cloudlet-console