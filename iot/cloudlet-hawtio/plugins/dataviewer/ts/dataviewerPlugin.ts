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
/// <reference path="dataviewerGlobals.ts"/>
/// <reference path="dataviewerHelpers.ts"/>
/// <reference path="model.ts"/>
module DataViewer {

  export var _module = angular.module(DataViewer.pluginName, ['hawtio-core', 'angularUtils.directives.dirPagination']);

  var tab = undefined;
  var httpEndpoint, apiEndpoint = undefined;

  _module.config(["$locationProvider", "$routeProvider", "HawtioNavBuilderProvider",
    ($locationProvider, $routeProvider: ng.route.IRouteProvider, builder: HawtioMainNav.BuilderFactory) => {
      
    var documentSubTab = builder.create().id(DataViewer.documentSubTabId)
                        .rank(30)
                        .page(() => builder.join(DataViewer.templatePath, "main.html"))                      
                        .href(() => DataViewer.context + '/document')
                        .title(() => "Document")
                        .show(() => true)
                        .build();                      
                              
    tab = builder.create()
      .id(DataViewer.pluginName)
      .title(() => "Data Viewer")
      .href(() => DataViewer.context)
      .tabs(documentSubTab)
      .build();
      
            
    builder.configureRouting($routeProvider, tab);
    $locationProvider.html5Mode(true);
  }]);
  
  _module.factory('DataViewerService', ['$http', 'HawtioNav', ($http:ng.IHttpService, nav: HawtioMainNav.Registry) => {
    return {
      getCollections: () => {        
        return $http.get(DataViewer.getService(nav.selected().tabs).getHttpEndpoint() + 'mongodb'); 
      },      
      findByQuery: (collectionName: string, query: Query) => {
        return $http.post(DataViewer.getService(nav.selected().tabs).getApiEndpoint() + 'findByQuery/' + collectionName, query);
      },
      countByQuery: (collectionName: string, query: Query) => {
        return $http.post(DataViewer.getService(nav.selected().tabs).getApiEndpoint() + 'countByQuery/' + collectionName, query);
      }
    };
  }]);  
  
  _module.filter('getProperty', function() {
    
    function getProperty(obj, prop) {
      if (!prop.includes('.')) {
        return obj[prop];
      }
      
      var parts = prop.split('.'),
          last = parts.pop(),
          l = parts.length,
          i = 1,
          current = parts[0];
          
      while((obj = obj[current]) && i < l) {
          current = parts[i];
          i++;
      }
  
      if(obj) {
          return obj[last];
      }
    }  
    
    return function (item, property) {
      return getProperty(item, property);
    };    
  });

  _module.run(["HawtioNav", (HawtioNav: HawtioMainNav.Registry) => {                      
    HawtioNav.add(tab);
    log.debug("loaded");
  }]);


  hawtioPluginLoader.addModule(DataViewer.pluginName);
}
