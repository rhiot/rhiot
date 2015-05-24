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
module DataViewer {

  export interface IServiceUrls {
    getHttpEndpoint(): String;
    getApiEndpoint(): String;
  }
  
  export function getService(navItems: HawtioMainNav.NavItem[]) :IServiceUrls {
    
    var selectedItem = null;
    
    navItems.forEach((item) => {
      if (item.isSelected() === true) {
        selectedItem = item;
      }
    });
    
    switch (selectedItem.id) {
      case DataViewer.documentSubTabId:
        return <IServiceUrls> {
          getHttpEndpoint() {
            return DataViewer.docServiceHttpEndpoint;
          },
            
          getApiEndpoint() {
            return DataViewer.docServiceApiEndpoint;
          }
        };
        
      case DataViewer.messagestoreSubTabId:
        return <IServiceUrls> {
          getHttpEndpoint() {
            return DataViewer.messagestoreServiceHttpEndpoint;
          },
            
          getApiEndpoint() {
            return DataViewer.messagestoreServiceApiEndpoint;
          }
        };       
    
      default:
        return null;
    }
  }
}
