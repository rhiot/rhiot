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
var DevExample;
(function (DevExample) {
    DevExample._module = angular.module(DevExample.pluginName, []);
    var tab = undefined;
    DevExample._module.config(["$locationProvider", "$routeProvider", "HawtioNavBuilderProvider", function ($locationProvider, $routeProvider, builder) {
        tab = builder.create().id(DevExample.pluginName).title(function () { return "Test DevExample"; }).href(function () { return "/test_example"; }).subPath("Page 1", "page1", builder.join(DevExample.templatePath, "page1.html")).build();
        builder.configureRouting($routeProvider, tab);
    }]);
    DevExample._module.run(["HawtioNav", function (HawtioNav) {
        HawtioNav.add(tab);
        DevExample.log.debug("loaded");
    }]);
    hawtioPluginLoader.addModule(DevExample.pluginName);
})(DevExample || (DevExample = {}));
//# sourceMappingURL=examplePlugin.js.map