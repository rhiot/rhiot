# Rhiot release notes

## 0.1.2 (currently in progress)

## 0.1.1

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

## 0.1.0

- Initial version of the Camel based [IoT field gateway](https://github.com/rhiot/rhiot/tree/master/iot#camel-iot-gateway)
- Raspbian [installation scripts](https://github.com/rhiot/rhiot/tree/master/iot#installing-gateway-on-the-raspbian) for the Camel IoT Gateway
- [Heartbeats support](https://github.com/rhiot/rhiot/tree/master/iot#device-heartbeats) for the Camel IoT Gateway (including logging, MQTT and LED signals)
- [Camel Kura WiFi component](https://github.com/rhiot/rhiot/tree/master/iot#camel-kura-wifi-component)
- [Camel Tinkerforge component](https://github.com/rhiot/rhiot/tree/master/iot#camel-tinkerforge-component)
- [Camel Pi4j component](https://github.com/rhiot/rhiot/tree/master/iot#camel-pi4j-component)
- [Camel PubNub component](https://github.com/rhiot/rhiot/tree/master/iot#camel-pubnub-component)
- Alpha version of the [Cloudlets](https://github.com/rhiot/rhiot/tree/master/iot#cloudlets) backend services, currently including [Geofencing Cloudlet](https://github.com/rhiot/rhiot/tree/master/iot/cloudlet/geofencing) and [Hawt.io-based UI plugin for it](https://github.com/rhiot/rhiot/tree/master/iot/cloudlet/geofencing)
