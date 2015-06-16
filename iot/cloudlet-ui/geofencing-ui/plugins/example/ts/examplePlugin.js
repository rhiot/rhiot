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
/// <reference path="exampleGlobals.ts"/>
var Example;
(function (Example) {
    Example._module = angular.module(Example.pluginName, []);
    var tab = undefined;
    Example._module.config(["$locationProvider", "$routeProvider", "HawtioNavBuilderProvider", function ($locationProvider, $routeProvider, builder) {
        tab = builder.create().id(Example.pluginName).title(function () { return "Geofencing"; }).href(function () { return "/geofencing"; }).subPath("Routes", "routes", builder.join(Example.templatePath, "routes.html")).build();
        builder.configureRouting($routeProvider, tab);
        $locationProvider.html5Mode(true);
    }]);
    Example._module.run(["HawtioNav", function (HawtioNav) {
        HawtioNav.add(tab);
        Example.log.debug("loaded");
    }]);
    hawtioPluginLoader.addModule(Example.pluginName);
})(Example || (Example = {}));
//# sourceMappingURL=examplePlugin.js.map