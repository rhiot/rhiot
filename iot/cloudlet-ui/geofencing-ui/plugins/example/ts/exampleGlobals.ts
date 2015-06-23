/// Copyright 2014-2015 Red Hat, Inc. and/or its affiliates
/// and other contributors as indicated by the @author tags.
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///   http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.

/// <reference path="../../includes.ts"/>
/// <reference path="geofencing.ts"/>
module Example {

  export var pluginName = "hawtio-assembly";

  export var log: Logging.Logger = Logger.get(pluginName);

  export var templatePath = "plugins/example/html";

  export function geofencingCloudletApiBase() {
    var cloudUri = Geofencing.uriParam('cloudUri');
    var uri = cloudUri == null ? Geofencing.windowLocationHostname() : cloudUri;
    return 'http://' + uri + ':15001/api/geofencing';
  }

  export function cloudletApiBase() {
    var cloudUri = Geofencing.uriParam('cloudUri');
    var uri = cloudUri == null ? Geofencing.windowLocationHostname() : cloudUri;
    return 'http://' + uri + ':15001/api';
  }

}
