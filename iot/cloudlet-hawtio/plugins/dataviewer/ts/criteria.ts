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
/// <reference path="dataviewerPlugin.ts"/>
/// <reference path="model.ts"/>
module DataViewer {

  export var CriteriaController = _module.controller("CriteriaController", ["$rootScope", "$scope", "$log", ($rootScope, $scope, $log:ng.ILogService) => {  
    
    $scope.queryConditions = [new QueryCondition()]; 
    $scope.operators = Operator.getValues();
    
    $scope.addCondition = function() {   
      $scope.queryConditions.push(new QueryCondition());         
    };  
    
    $scope.removeCondition = function(index) {
      $scope.queryConditions.splice(index, 1);   
    };  
    
    $scope.search = function() {
      var q =  buildConditions($scope.queryConditions);
      $rootScope.$broadcast('search', q);  
    };
    
    $scope.toggleViewMode = function() {
      $rootScope.$broadcast('toggleViewMode');
    };    
    
    $rootScope.$on('collectionSelected', function(event, args) {    
      $scope.queryConditions = [new QueryCondition()];   
    });  
    
    function buildConditions(queryConditions: QueryCondition[]) {
      var q = {};
      queryConditions.forEach(function(element: QueryCondition) {        
        if (element.key && element.value) {
          if (element.operator !== "Equal") {
            q[element.key + "" + element.operator] = element.value;
          } else {
            q[element.key] = element.value;
          }  
        }      
      }, this); 
      
      return q;     
    };    
    
  }]);

}
