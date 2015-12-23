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
userNode = m2.createTextNode(os.environ["SONATYPE_USERNAME"])
passNode = m2.createTextNode(os.environ["SONATYPE_PASSWORD"])

sonatypeServerId.appendChild(idNode)
sonatypeServerUser.appendChild(userNode)
sonatypeServerPass.appendChild(passNode)

sonatypeServerNode.appendChild(sonatypeServerId)
sonatypeServerNode.appendChild(sonatypeServerUser)
sonatypeServerNode.appendChild(sonatypeServerPass)

serversNode.appendChild(sonatypeServerNode)

m2Str = m2.toxml()
f = open(homedir + '/.m2/mySettings.xml', 'w')
f.write(m2Str)
f.close()