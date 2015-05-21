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
var Example;
(function (Example) {
    Example.Page1Controller = Example._module.controller("Example.Page1Controller", ["$scope", "$http", function ($scope, $http) {
        $scope.target = "World!";
        $scope.clientSelected = function () {
            $http.get('http://localhost:15001/api/geofencing/routes/routes/' + $scope.selectedOption.id).success(function (data, status, headers, config) {
                $scope.routes = data.routes.map(function (val) {
                    return {
                        name: val,
                        id: val
                    };
                });
                if (data.routes.length > 0) {
                    $scope.selectedRoute = $scope.routes[0];
                }
            }).error(function (data, status, headers, config) {
                $scope.clients = 'Connection error';
            });
        };
        $http.get('http://localhost:15001/api/geofencing/routes/clients').success(function (data, status, headers, config) {
            $scope.clients = data.clients.map(function (val) {
                return {
                    name: val,
                    id: val
                };
            });
            if (data.clients.length > 0) {
                $scope.selectedOption = $scope.clients[0];
                $scope.clientSelected();
            }
        }).error(function (data, status, headers, config) {
            $scope.clients = 'Connection error';
        });
    }]);
})(Example || (Example = {}));
//# sourceMappingURL=page1.js.map