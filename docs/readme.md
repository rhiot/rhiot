# Rhiot - the messaging platform for the Internet Of Things

<a href="https://github.com/rhiot/rhiot"><img src="../rhiot.png" align="left" height="280" hspace="30"></a>
Rhiot is a messaging platform for the Internet Of Things. We are focused on an adoption of the
[Red Hat JBoss middleware portfolio](http://www.redhat.com/en/technologies/jboss-middleware) to provide solutions to
the common IoT-related challenges.

**We are currently in a process of moving Rhiot documentation to GitBook. Our new documentation site can be found
[here](https://rhiot.gitbooks.io/rhiotdocumentation/content/).**

## Table Of Contents

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Rhiot Cloud](#rhiot-cloud)
  - [Architecture](#architecture)
  - [Dockerized Rhiot Cloud](#dockerized-rhiot-cloud)
  - [Device management cloudlet](#device-management-cloudlet)
    - [Running the device management cloudlet](#running-the-device-management-cloudlet)
    - [Device management REST API](#device-management-rest-api)
      - [Listing devices](#listing-devices)
      - [Reading particular device's metadata](#reading-particular-devices-metadata)
      - [Disconnected devices](#disconnected-devices)
      - [Deregistering all the devices](#deregistering-all-the-devices)
      - [Deregistering single device](#deregistering-single-device)
      - [Reading device's details](#reading-devices-details)
      - [Creating virtual devices](#creating-virtual-devices)
      - [Intercepting REST API requests](#intercepting-rest-api-requests)
    - [Device management web UI](#device-management-web-ui)
      - [Listing devices](#listing-devices-1)
      - [Sending heartbeat to the device](#sending-heartbeat-to-the-device)
      - [Deregistering devices](#deregistering-devices)
      - [Listing device details](#listing-device-details)
      - [Creating virtual devices](#creating-virtual-devices-1)
    - [Accessing LWM2M server directly](#accessing-lwm2m-server-directly)
    - [Device registry](#device-registry)
      - [Registry cache](#registry-cache)
    - [Clustering Device Management Cloudlet](#clustering-device-management-cloudlet)
    - [Devices data analytics](#devices-data-analytics)
  - [Geofencing cloudlet](#geofencing-cloudlet)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Rhiot Cloud

The Internet of Things is all about the communication and messaging. The devices connected to the IoT system have to
connect to the kind of centralized hub that allows them to exchange their data with the other devices and backend
services. The device that can't be properly connected to the rest of the application ecosystem, is useless from the IoT point
 of view.

If you are looking for such centralized event hub, Rhiot project provides such central bus in the form of the
*Rhiot Cloud*. Rhiot Cloud is the set of the backend (micro)services and UI application used to managed these.

### Architecture

The high-level architecture diagram of the Rhiot Cloud is presented on the image below:

 <img src="images/rhiot-cloud-arch.png" align="center" height="600">

*Cloudlets* are server-side microservices that come with some common functionalities required by the IoT systems. *Cloudlets
UI plugins* are [Hawt.io](http://hawt.io)-based plugins which provides spiffy web UI for the cloudlets back-end services. *Cloudlet
Console* is the web application assembling all the Cloudlets UI plugins. The *Rhiot Cloud* then is the
complete cloud-based installation setup including Cloudlet Console, Cloudlets backend services and all the other necessary
services (like database servers) deployed to the server of your choice.

Notice that we assume that cloudlets are dockerized and deployed as the Docker containers. Also the HTTP REST API has been listed
at the top of the diagram not without the reason - we think of the REST API as the first-class citizen considering the
access to the Rhiot Cloud.

### Dockerized Rhiot Cloud

We recommend to run the Rhiot Cloud using the Docker container. We love Docker and believe that containers are the
future of the applications deployment. To install the Rhiot Cloud on the Linux server of your choice, just execute the
following command:

    GOOGLE_OAUTH_CLIENT_ID=foo.apps.googleusercontent.com \
    GOOGLE_OAUTH_CLIENT_SECRET=yourSecret \
    GOOGLE_OAUTH_REDIRECT_URI=http://myapp.com \
      bash <(curl -s https://raw.githubusercontent.com/rhiot/rhiot/master/cloudlets/deployment/rhiot-cloud.sh)

The script above installs the proper version of Docker server. Keep in mind that the minimal Docker version required by
Rhiot Cloud is 1.8.2 - if the older version of the Docker is installed, our script will upgrade your Docker server. After
Docker server is properly installed, our script downloads and starts the Cloudlet Console, device management cloudlet,
geofencing cloudlet and MongoDB server containers.

By default Rhiot Cloud runs the console UI using the development HTTP port 9000. If you want to change it, use the `HTTP_PORT`
environment variable:

    HTTP_PORT=80 \
      ...
      bash <(curl -s https://raw.githubusercontent.com/rhiot/rhiot/master/cloudlets/deployment/rhiot-cloud.sh)

Environment variables starting with `GOOGLE_OAUTH` prefix are used to configure the Google OAuth authentication
used by the Cloudlet Console. You have to create the Google application in the
[Developers Console](https://console.developers.google.com) to get your client identifier, secret and configure the
accepted redirect URIs. If `GOOGLE_OAUTH_REDIRECT_URI` variable is net given, `http://localhost:9000` will be used.

Rhiot Cloud relies on the MongoDB to store some of the data processed by it. For example MongoDB backend is the default
store used by the device management cloudlet's Leshan server. By default the MongoDB data is stored in the `mongodb_data`
volume container. If such volume doesn't exist, Rhiot Cloud script will create it for you.

### Device management cloudlet

The foundation of the every IoT solution is the device management system. Without the centralized coordination of your
*things*, you can't properly orchestrate how your devices communicate with each other. Also the effective monitoring of
the IoT system, without the devices registered in the centralized cloud, becomes almost impossible.

Device Management Cloudlet provides backend service for registering and tracking devices connected to the Rhiot Cloud.
Under the hood Device Management Cloudlet uses [Eclipse Leshan](https://projects.eclipse.org/projects/iot.leshan), the
open source implementation of the [LWM2M](https://en.wikipedia.org/wiki/OMA_LWM2M) protocol. LWM2M becomes the standard
for the IoT devices management so we decided to make it a heart of the Rhiot device service.

The diagram below presents the high-level overview of the device cloudlet architecture.

<img src="images/cloudlet-device-arch.png" align="center" height="600">

#### Running the device management cloudlet

The device management cloudlet is distributed as a fat jar. Its Maven coordinates are
`io.rhiot/rhiot-cloudlet-device/0.1.1`. The dockerized artifact is available in Docker Hub as
[rhiot/cloudlet-device:0.1.1](https://hub.docker.com/r/rhiot/cloudlet-device). In order to start the device management
microservice, just run it as a fat jar...

    java -jar rhiot-cloudlet-device:0.1.1.jar

...or as the Docker container...

    docker run -d io.rhiot/cloudlet-device/0.1.1

#### Device management REST API

The device management cloudlet exposes REST API that can be used to work with the devices. By default the device
management REST API is exposed using the following base URI - `http:0.0.0.0:15000`. You can change the port of the
REST API using the `api_rest_port` environment variable. For example the snippet below exposes the REST API on the port
16000:

    docker run -d -e api_rest_port=16000 -p 16000:16000 io.rhiot/cloudlet-device/0.1.1

##### Listing devices

To list the devices registered to the cloud (together with their metadata) send the `GET` request to the
`/device` URI. For example executing the following command returns the list of the devices in the form of the list
serialized to the JSON format:

    $ curl http://rhiot.net:15000/device
    {"devices":
      [{"registrationDate":1439822565254,
      "address":"127.0.0.1",
      "port":1103,
      "registrationEndpointAddress":"0.0.0.0:5683",
      "lifeTimeInSec":86400,
      "lwM2mVersion":"1.0",
      "bindingMode":"U",
      "endpoint":"myFancyDevice",
      "registrationId":"7OjdvHCVUb",
      "objectLinks":[{"url":"/",
        "attributes":{"rt":"oma.lwm2m"},
        "path":"/"},
        ...],
      "alive":true}]}

##### Reading particular device's metadata

In order to read the metadata of the particular device identified with the given ID, send the `GET` request to the `/device/ID`
URI. For example to read the metadata of the device with the ID equal to `myDevice001`, execute the following command:

    $ curl http://rhiot.net:15000/device/myDevice001
    {"device":
      {"registrationDate":1441959646566,"address":"127.0.0.1","port":1111,
      "registrationEndpointAddress":"0.0.0.0:5683", "lifeTimeInSec":31536000,
      "lwM2mVersion":"1.0","bindingMode":"U","endpoint":"myDevice001",
      "registrationId":"2OMPXtg6lX", "objectLinks": ... ,"alive":true}
    }

##### Disconnected devices

Devices registered in the Rhiot cloud can be in the *connected* or *disconnected* status. Connected devices can exchange
messages with the cloud, while disconnected can't. Disconnection usually occurs when device temporarily lost the network
connectivity.

To return the list of identifiers of the disconnected devices send the `GET` request to the following URL -
`/device/disconnected`. In the response you will receive the list of the identifiers of the devices
that have not send the heartbeat signal to the device management cloudlet for the given *disconnection period* (one minute by
default). The list will be formatted as the JSON document similar to the following one:

    $ curl http://rhiot.net:15000/device/disconnected
    {"disconnectedDevices": ["device1", "device2", ...]}

The disconnection period can be changed globally using the `disconnectionPeriod` environment variable indicating the
disconnection period value in miliseconds. For example the snippet below sets the disconnection period to 20 seconds:

    docker run -d -e disconnectionPeriod=20000 io.rhiot/cloudlet-device/0.1.1

The device which is running and operational should periodically send the hearbeat signal to the device cloudlet in order to avoid
being marked as disconnected. You can do it be sending the `GET` request to the
`/device/DEVICE_ID/heartbeat` URI. If the heartbeat has been successfully send to the cloud,
you will receive the HTTP response similar to the following one:

    $ curl http://rhiot.net:15000/device/myDeviceID/heartbeat
    {"status": "success"}

Keep also in mind that sending the regular LWM2M update by the client device to the LWM2M server works the same as sending
the heartbeat update via the REST API.

##### Deregistering all the devices

In order to deregister all the devices from the cloud, send the `DELETE` request to the `/device` URI. For example:

    $ curl -XDELETE http://rhiot.net:15000/device
    {"status":"success"}

##### Deregistering single device

Sometimes you would like to explicitly remove the particular registered device from the cloudlet database. In such case execute the
`DELETE` request against the `/device/DEVICE_ID` URI. For example to remove the device with the ID equal to `foo`, execute
the following command:

    $ curl -XDELETE http://rhiot.net:15000/device/foo
    {"status":"success"}

##### Reading device's details

LWM2M protocol allows you to read the values of the various metrics from the managed device. The basic metrics includes
device's manufacturer name, model, serial number, firmware version and so forth. In order to read the device details,
send `GET` request to the `/device/myDeviceID/details` URI. For example to read the details of the device
identified by the `myDeviceID`, execute the following command:

    $ curl http://rhiot.net:15000/device/myDeviceID/details
    {"deviceDetails":
      {"serialNumber":"Serial-0cc28150-3a09-4acc-b12d-d9101b8a29d2",
      "modelNumber":"Virtual device",
      "firmwareVersion":"1.0.0",
      "manufacturer":"Rhiot"}
    }

The `/device/ID/details` call connects to the given device, collects the metrics and returns those metrics wrapped into
the JSON response. You can also collect individual metrics using the following URIs:

| Metric                   | URI                      | Description   |
|:-------------------------|:-------------------------       |:------------- |
| Manufacturer | `/device/deviceId/manufacturer`                | Device's manufacturer. For example `Raspberry Pi`. |
| Model number         | `/device/deviceId/modelNumber` | Device's model. For example `2 B+`. |
| Serial number | `/device/deviceId/serialNumber` | Unique string identifying the particular piece of the hardware. |
| Firmware number   | `/device/deviceId/firmwareVersion`  | The text identifying the version of the software that device is running. Can include both operating system and applications' versions. |

For example to read the version of the software used by your device, execute the following `GET` request:

     $ curl http://rhiot.net:15000/device/foo/firmwareVersion
    {"firmwareVersion":"1.0.0"}

Keep in mind that the metric values read by these operations are saved to the metrics database and can be accessed later on
(see [Devices Data Analytics](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#devices-data-analytics)). Also if
the device is disconnected at the moment when the REST API is called, the value will be read from the metrics history database,
instead of the real device. If there is no historical value available for the given device and metric, the
`unknown - device disconnected` value will be returned for it. For example:

     $ curl http://rhiot.net:15000/device/foo/firmwareVersion
    {"firmwareVersion":"unknown - device disconnected"}

##### Creating virtual devices

Device Cloudlet offers you the option to create the *virtual devices*. Virtual devices can be used to represent the clients which
can't use LWM2M API. For example the Android phone could use REST calls only to create and maintain the projection of
itself as the virtual device even if you can't install LWM2M client on that device.

To create the virtual device, send the `POST` request to the `/client` URI. For example:

    curl -X POST -d '{ "clientId": "myVirtualDeviceId"}' http://rhiot.net:15000/client
    {"Status":"Success"}%

Starting from this point your the virtual device identified as `myVirtualDeviceId` will be registered in the device
cloudlet LWM2M server. And of course you can send the heartbeats signals to the virtual device to indicate that the
device is still connected to the Rhiot Cloud.

    $ curl http://rhiot.net:15000/device/myDeviceID/heartbeat
    {"status": "success"}

##### Intercepting REST API requests

If you would like to intercept the HTTP communication between the HTTP client and the REST API (for example in order to add the
security checks), just add your custom implementation of the
[`HttpExchangeInterceptor`](https://github.com/rhiot/rhiot/blob/master/vertx/src/main/groovy/io/rhiot/vertx/web/HttpExchangeInterceptor.groovy)
interface to your classpath...

    package com.example

    import io.rhiot.vertx.web.HttpExchangeInterceptor
    import io.vertx.groovy.ext.web.RoutingContext

    public class MockHttpExchangeInterceptor implements HttpExchangeInterceptor {

        @Override
        public void intercept(RoutingContext routingContext) {
            String token = routingContext.request().getHeader('security_token');
            if(...) { // token is not valid
                routingContext.response().end("Invalid security token!");
            }
        }

    }

...and set the `application_package` property to the base package of your application. For example for the snippet
above the base package could be set as follows:

    docker run -d -e application_package=com.example com.example/rhiot-cloudlet-device-customized

#### Device management web UI

Rhiot Cloudlet Console is the web user interface on the top of the device management REST API. The web UI makes it easier
to monitor and manage your devices using the web brower, the mobile phone or the tablet.

##### Listing devices

To list the devices
using the cloudlet console navigate to the `Devices` tab. You will see the list of the devices registered to the
device management cloudlet. Devices with the icon representing white (empty) heart are the disconnected ones.

<img src="images/console-device-list.png" align="center" height="400" hspace="30" >

##### Sending heartbeat to the device

In order to send heartbeat message to the given device and make it visible as connected again, you can use the
`Send heartbeat` button near the device's icon.

<img src="images/console-device-heartbeat.png" align="center" height="400" hspace="30" >

##### Deregistering devices

If you would like deregister the device from the cloud, click the `Deregister` button near the device icon.

##### Listing device details

If you click on the device name, the web UI will fetch and display the device details metrics:

<img src="images/console-device-details.png" align="center" height="400" hspace="30" >

##### Creating virtual devices

If you don't have any devices nearby at the moment and still want to play with the Device management Cloudlet, don't
worry - you can still create the new virtual device using the web UI.

<img src="images/console-device-prompt.png" align="center" height="400" hspace="30" >

All you need to do is to enter the unique identifier of the device:

<img src="images/console-device-create.png" align="center" height="400" hspace="30" >

The device will be registered in the Rhiot Cloud and visible as soon as you click the `Create virtual device` button.

<img src="images/console-device-created.png" align="center" height="400" hspace="30" >

#### Accessing LWM2M server directly

While we suggest to use the universal REST API whenever possible, you can definitely use the LWM2M server directly.
By default the LWM2M server API is exposed using the default IANA port i.e. 5683. The embedded LWM2M server is started
together with the cloudlet.

In order to use custom LWM2M server port, set the `lwm2m_port` environment variable when starting the device
management cloudlet (or Rhiot Cloud). For example:

    docker run -d -e lwm2m_port=16000 -p 16000:16000 io.rhiot/cloudlet-device/0.1.1

#### Device registry

Device registry is used by Leshan to store the information about the managed devices. By default the device cloudlet uses
the MongoDB registry. The MongoDB client can be configured using the [Steroids MongoDB module](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#injecting-mongodb-client).

##### Registry cache

As the access to the device information is crucial for all the IoT systems, it should have been implemented as efficiently
as possible. As devices information doesn't change very often, it should be cached in the memory whenever possible. Device
Management Cloudlet uses the [Infinispan](http://infinispan.org) cache cluster under the hood, to provide the faster access
to the device information. The Infinispan cache used is clustered using JGroups, so the cached information
remains up-to-date even when many Device Manager Cloudlet instances are executed in the cluster.

#### Clustering Device Management Cloudlet

Device Management Cloudlet has been designed with the scalability in mind. Default configuration of the cloudlet allows
you to run it in the cluster, behind the load balancer of your choice. The default MongoDB device registry will be
shared by all the cloudlet instances in the cluster. Also the device registry cache used internally by Device Management Cloudlet
will be automatically synchronized between the deployed cloudlet instances. All you need to do, is to be sure that you have
the [IP multicast](https://en.wikipedia.org/wiki/IP_multicast) enabled for your local network, so the JGroups cluster can
be established between the cloudlet instances.

Keep in mind that each clustered instance of the Device Management Cloudlet exposes both REST and LWM2M API, so you can
take advantage of load balancing over all the APIs available.

#### Devices data analytics

LWM2M protocol provides you the way to read the metrics' values from the devices. However in order to perform the search
queries against those values, you have to store those in a centralized storage. For example if you would like to find all the
devices with the firmware version smaller than `x.y.z`, you have to store all the firmware version of your devices in
the centralized database, then execute a query against that database. Otherwise you will be forced to connect to the all of
your devices using the LWM2M protocol and ask each device to provide its firmware version number. Asking millions of
the devices connected to your system to provide you their firmware version is far from the ideal in the terms of the
efficiency.

The device cloudlet stores each device metric value read from the LWM2M server via the REST API in the dedicated analytics
store. It basically means that whenever you call the REST API to read the device metric, the value read from the
device is stored in the database. For example the following API call will not only return the firmware version of the
device identified by `myDevice`, but also will remember this value in the analytics database:

    $ curl http://rhiot.net/device/myDevice/firmwareVersion
    {"firmwareVersion": "1.0.0"}

By default the historical metrics data is saved in the [MongoDB](https://www.mongodb.org) database. The default database name is
`DeviceCloudlet`, while the historical values of the read metrics are saved to the `DeviceMetrics` collection. Device
Cloudlet reuses the MongoDB connection settings used by the MongoDB LWM2M device registry store.

### Geofencing cloudlet

Geofencing cloudlet provides backend cloud service for collecting and the basic analysis of the GPS data.
