/// <reference path="../../includes.d.ts" />
declare module Example {
    var pluginName: string;
    var log: Logging.Logger;
    var templatePath: string;
    function geofencingCloudletApiBase(): string;
    function cloudletApiBase(): string;
    function uriParam(name: any): string;
}
