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

/// <reference path="examplePlugin.ts"/>
module Example {

  export var Page1Controller = _module.controller("Example.Page1Controller", ["$scope", "$http", ($scope, $http) => {
    $scope.clientSelected = function() {
      $http.get('http://' + window.location.hostname + ':15001/api/geofencing/routes/routes/' + $scope.selectedOption.id).
          success(function(data, status, headers, config) {
            $scope.routes = data.routes.map(function (val) {
              return {
                name: val.created,
                id: val.id
              };
            });
            if(data.routes.length > 0) {
              $scope.selectedRoute = $scope.routes[0];
            }
          }).
          error(function(data, status, headers, config) {
            $scope.flash = 'Cannot connect to the geofencing service.';
          });
    };
    $scope.routeSelected = function () {
          $http.get('http://' + window.location.hostname + ':15001/api/geofencing/routes/routeUrl/' + $scope.selectedRoute.id).success(function (data, status, headers, config) {
              $scope.routeUrl = data.url;
          }).error(function (data, status, headers, config) {
              $scope.flash = 'Cannot connect to the geofencing service.';
          });
    };
    $http.get('http://' + window.location.hostname + ':15001/api/geofencing/routes/clients').
        success(function(data, status, headers, config) {
          $scope.clients = data.clients.map(function (val) {
            return {
              name: val,
              id: val
            };
          });
          if(data.clients.length > 0) {
            $scope.selectedOption = $scope.clients[0];
            $scope.clientSelected();
          }
        }).
        error(function(data, status, headers, config) {
          $scope.flash = 'Cannot connect to the geofencing service.';
        });
  }]);

}
