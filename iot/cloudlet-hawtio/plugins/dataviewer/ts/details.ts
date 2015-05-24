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

/// <reference path="dataviewerPlugin.ts"/>
module DataViewer {

  export var DetailsController = _module.controller("DetailsController", ["$q", "$rootScope", "$scope", "$log", "DataViewerService", ($q:ng.IQService, $rootScope, $scope, $log:ng.ILogService, DataViewerService) => {
    $scope.isTableView = true;    
    $scope.collectionName = '';
    $scope.query = new Query();  
    
    function init() {
      $scope.documents = [];
      
      $scope.query = new Query();
      $scope.query.query = {};
      
      $scope.headers = [];
      $scope.total = 0;      
      $scope.pagination = {
          current: 1
      };      
    }
    
    init();
   
    $scope.pageChanged = function(newPageNumber) {
      $scope.query.page = newPageNumber - 1;
      search($scope.collectionName, $scope.query);  
    };  
    
    $rootScope.$on('toggleViewMode', function(event, args) {    
      $scope.isTableView = !$scope.isTableView; 
    });        
        
    $rootScope.$on('collectionSelected', function(event, args) {      
      $scope.collectionName = args;
      
      init();

      if ($scope.collectionName !== undefined) {   
        count($scope.collectionName, $scope.query).then(function(success) {
          $scope.total = success;
          search($scope.collectionName, $scope.query).then(function() {
            if ($scope.documents[0] !== undefined ) {
              getHeaders(null, $scope.documents[0]);
            }
          }); 
        });
      }
    });
    
    $rootScope.$on('search', function(event, args) { 
      $scope.query.query = args;
 
      if ($scope.collectionName !== undefined) {      
        count($scope.collectionName, $scope.query).then(function(success) {
          $scope.total = success;
          search($scope.collectionName, $scope.query);  
        });
      }             
    });     
    
    function search(collectionName, query) {
      var deferred = $q.defer();
      DataViewerService.findByQuery(collectionName, query).then(function(success) {
        if (success.data !== undefined) {                 
          $scope.documents = success.data;
          deferred.resolve(success.data);
        }
      }, function(error) {
        deferred.reject(error);
      });  
      
      return deferred.promise;      
    }   
    
    function count(collectionName, query) { 
      var deferred = $q.defer();
      DataViewerService.countByQuery(collectionName, query).then(function(success) {
        if (success.data !== undefined) {       
          deferred.resolve(success.data); 
        }
      }, function(error) {
        deferred.reject(error);
      });             
      
      return deferred.promise;
    }    
    
    function getHeaders(property, object) {
      var finalPropertyName;
      for (var propertyName in object) {
        if (object.hasOwnProperty(propertyName)) {
          
          if (typeof object[propertyName] === 'array') {
            continue; 
          }
          
          if (property !== null) {
            finalPropertyName = property + '.' + propertyName;
          }          
          
          if (typeof object[propertyName] === 'object') {
            getHeaders(propertyName, object[propertyName]);
            continue; 
          }
          
          if (finalPropertyName !== undefined) {
            $scope.headers.push(finalPropertyName);
          } else {
            $scope.headers.push(propertyName);
          }
        }
      }    
    }           
  }]);
}
