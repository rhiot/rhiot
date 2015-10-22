# Rhiot release notes

## 0.1.3 (currently in progress)

## 0.1.2  (2015-10-22)

- Deprecated BU353 component on the behalf of the GPSD component (#232)
- Added GPS client coordinates type converter (#213)
- Fixed: BU353 returns "FileNotFoundException: /dev/ttyUSB0 (Device or resource busy)" (#210)
- Rhiot now supports reading Spring Boot application.properties file (#226)
- Renamed com.github.camellabs.iot.vertx.camel.GroovyCamelVerticle to io.rhiot.vertx.camel.GroovyCamelVerticle (#207)
- Moved camel-kura component from com.github.camellabs to io.rhiot package (#195)
- Device detection is performed in parallel (#218)
- Added "scan" command to the deployer (#217)
- Deployer now allows to specify customized fat jar (#216)
- Deployer now downloads the same gateway version as deployer version (#215)
- Deployer now detects devices from multiple interfaces (#214)
- Deployer now scans OSX network interfaces (#202)
- Device Cloudlet MongoDB connection now timeouts faster (#191)
- Add [Webcam camel component](https://github.com/rhiot/rhiot/issues/239), thx [@levackt](https://github.com/levackt) (#239)


## 0.1.1  (2015-09-15)

- Changed project name from *Camel Labs* to *Rhiot*
- Added [Dockerized Rhiot Cloud](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#dockerized-rhiot-cloud) (#160).
- Added [device management cloudlet](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#device-management-cloudlet) (#114).
- Added web UI (aka [Cloudlet Console](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#device-management-web-ui)) (#129).
- Created [Rhiot Cloud demo site](http://rhiot.net) (#155).
- Added [camel-gps-bu353](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-gps-bu353-component) component (#93).
- Migrated [Gateway](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-iot-gateway) core from Spring Boot to Vert.x (#141, #120).
- [Gateway should start Jolokia REST API on port 8778, not 8080](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#monitoring-gateway-with-jolokia) (#143).
- Releasing [Gateway](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-iot-gateway) core and application artifacts separately (#126).
- Renamed `camellabs.iot.gateway.heartbeat.rate property` to `camellabs_iot_gateway_heartbeat_rate` (#124).
- Gateway [deletes logs after N megabytes limit is exceeded](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#gateway-logger-configuration) (#104, #95).
- Gateway [reads properties from the `/etc/default/camel-labs-iot-gateway`](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#configuration-of-the-gateway) (#98).

## 0.1.0  (2015-06-02)

- Initial version of the Camel based [IoT field gateway](https://github.com/rhiot/rhiot/tree/master/iot#camel-iot-gateway)
- Raspbian [installation scripts](https://github.com/rhiot/rhiot/tree/master/iot#installing-gateway-on-the-raspbian) for the Camel IoT Gateway
- [Heartbeats support](https://github.com/rhiot/rhiot/tree/master/iot#device-heartbeats) for the Camel IoT Gateway (including logging, MQTT and LED signals)
- [Camel Kura WiFi component](https://github.com/rhiot/rhiot/tree/master/iot#camel-kura-wifi-component)
- [Camel Tinkerforge component](https://github.com/rhiot/rhiot/tree/master/iot#camel-tinkerforge-component)
- [Camel Pi4j component](https://github.com/rhiot/rhiot/tree/master/iot#camel-pi4j-component)
- [Camel PubNub component](https://github.com/rhiot/rhiot/tree/master/iot#camel-pubnub-component)
- Alpha version of the [Cloudlets](https://github.com/rhiot/rhiot/tree/master/iot#cloudlets) backend services, currently including [Geofencing Cloudlet](https://github.com/rhiot/rhiot/tree/master/iot/cloudlet/geofencing) and [Hawt.io-based UI plugin for it](https://github.com/rhiot/rhiot/tree/master/iot/cloudlet/geofencing)
