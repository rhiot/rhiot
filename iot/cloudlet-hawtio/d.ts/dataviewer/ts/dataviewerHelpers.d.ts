/// <reference path="../../includes.d.ts" />
/// <reference path="dataviewerGlobals.d.ts" />
declare module DataViewer {
    interface IServiceUrls {
        getHttpEndpoint(): String;
        getApiEndpoint(): String;
    }
    function getService(navItems: HawtioMainNav.NavItem[]): IServiceUrls;
}
