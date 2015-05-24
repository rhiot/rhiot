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
module DataViewer {

  export var pluginName = "DataViewer";

  export var log: Logging.Logger = Logger.get(pluginName);

  export var templatePath = "plugins/dataviewer/html";
  
  export var context = '/dataviewer';
  
  // Tab ids
  
  export var documentSubTabId = 'documentSubTab';
  
  export var messagestoreSubTabId = 'messagestoreSubTab';
  
  // Services
  
  export var docServiceHttpEndpoint = 'http://localhost:15000/';
  
  export var docServiceApiEndpoint = 'http://localhost:15001/api/document/';
  
  export var messagestoreServiceHttpEndpoint = 'http://localhost:15006/';
  
  export var messagestoreServiceApiEndpoint = 'http://localhost:15007/api/document/';  
}
