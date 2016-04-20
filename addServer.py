#!/usr/bin/env python

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

import sys
import os
import os.path
import xml.dom.minidom

print 'Editing Maven settings...'

if os.environ["TRAVIS_SECURE_ENV_VARS"] == "false":
  print "no secure env vars available, skipping deployment"
  sys.exit()

homedir = os.path.expanduser("~")

m2 = xml.dom.minidom.parse(homedir + '/.m2/settings.xml')
settings = m2.getElementsByTagName("settings")[0]

serversNodes = settings.getElementsByTagName("servers")
if not serversNodes:
  serversNode = m2.createElement("servers")
  settings.appendChild(serversNode)
else:
  serversNode = serversNodes[0]

sonatypeServerNode = m2.createElement("server")
sonatypeServerId = m2.createElement("id")
sonatypeServerUser = m2.createElement("username")
sonatypeServerPass = m2.createElement("password")

idNode = m2.createTextNode("ossrh")
print 'Reading Sonatype username...'
userNode = m2.createTextNode(os.environ["SONATYPE_USERNAME"])
print 'Reading Sonatype password...'
passNode = m2.createTextNode(os.environ["SONATYPE_PASSWORD"])

sonatypeServerId.appendChild(idNode)
sonatypeServerUser.appendChild(userNode)
sonatypeServerPass.appendChild(passNode)

sonatypeServerNode.appendChild(sonatypeServerId)
sonatypeServerNode.appendChild(sonatypeServerUser)
sonatypeServerNode.appendChild(sonatypeServerPass)

serversNode.appendChild(sonatypeServerNode)

print 'Added Sonatype configuration.'

dockerHub = m2.createElement("server")
dockerHubId = m2.createElement("id")
dockerHubUsername = m2.createElement("username")
dockerHubPassword = m2.createElement("password")

dockerHubIdNode = m2.createTextNode("registry.hub.docker.com")
dockerHubUsernameNode = m2.createTextNode(os.environ["DOCKER_USERNAME"])
dockerHubPasswordNode = m2.createTextNode(os.environ["DOCKER_PASSWORD"])

dockerHubId.appendChild(dockerHubIdNode)
dockerHubUsername.appendChild(dockerHubUsernameNode)
dockerHubPassword.appendChild(dockerHubPasswordNode)

dockerHub.appendChild(dockerHubId)
dockerHub.appendChild(dockerHubUsername)
dockerHub.appendChild(dockerHubPassword)

serversNode.appendChild(dockerHub)

print 'Added DockerHub configuration.'

m2Str = m2.toxml()
f = open(homedir + '/.m2/mySettings.xml', 'w')
f.write(m2Str)
f.close()