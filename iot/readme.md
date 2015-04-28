# Camel IoT Labs

<a href="https://github.com/camel-labs/camel-labs"><img src="../camel-labs.png" align="left" height="80" hspace="30"></a>
Camel IoT Labs project covers modules providing the Internet Of Things functionalities related to the 
[Apache Camel](http://camel.apache.org).
<br><br>

## Camel IoT components

Camel IoT Labs brings some extra components for the Apache Camel intended to make both device- and server-side IoT
development easier.

### Camel Kura Wifi component

Camel Kura WiFi component can be used to retrieve the information about the WiFi access spots available within the device
range. Under the hood Kura Wifi component uses Kura `org.eclipse.kura.net.NetworkService`. Currently Kura WiFi component
supports only the consumer endpoints.

#### Maven dependency

Maven users should add the following dependency to their POM file:

    <dependency>
      <groupId>com.github.camel-labs</groupId>
      <artifactId>camel-kura</artifactId>
      <version>0.0.0</version>
    </dependency>
    
#### URI format

    kura:networkInterface/ssid
    
Where both `networkInterface` and `ssid` can be replaced with the `*` wildcard matching respectively all the network 
interfaces and SSIDs.

For example to read all the SSID available near the device, the following route can be used:

    from("kura:*/*").to("mock:SSIDs");

The Kura WiFi consumer returns the list of the `org.eclipse.kura.net.wifi.WifiAccessPoint` classes returned as a result
of the WiFi scan:

    WifiAccessPoint[] accessPoints = consumerTemplate.receiveBody("kura:wlan0/*", WifiAccessPoint[].class);

#### Options

| Option                 | Default value                                                           | Description   |
|:---------------------- |:----------------------------------------------------------------------- |:------------- |
| `accessPointsProvider` | `com.github.camellabs.iot.component.kura.wifi.KuraAccessPointsProvider` | `com.github.camellabs.iot.component.kura.wifi.AccessPointsProvider` strategy instance registry reference used to resolve the list of the access points available to consume. |

### Camel Raspberry Pi component

#### Library Depedency
This module uses [pi4j](http://pi4j.com/) library - version 1.0

#### Maven Dependency
```
<dependency>
    <groupId>com.github.camel-labs</groupId>
    <artifactId>camel-raspberry</artifactId>
    <version>x.x.x</version>
    <!-- use the same version as your Camel core version -->
</dependency>
```

#### GPIO Endpoint
##### URI Format
```
raspberrypi-gpio://gpioId[?options]
```

##### URI Options
##### Headers
##### Body

#### I2C Endpoint
##### URI Format
```
raspberrypi-i2c://(0x)busId/(0x)deviceId[?options]
```

##### URI Options
##### URI Options
##### Headers
##### Body

::: the component documentation here :::

### Camel Device IO component

#### Library Depedency
This module uses [Device I/O API/Impl](https://wiki.openjdk.java.net/display/dio/Main) library - version 1.0.1

#### Maven Dependency
```
<dependency>
    <groupId>com.github.camel-labs</groupId>
    <artifactId>camel-device-io</artifactId>
    <version>x.x.x</version>
    <!-- use the same version as your Camel core version -->
</dependency>
```

#### GPIO Endpoint
##### URI Format
```
deviceio-gpio://gpioId[?options]
```

##### URI Options
##### Headers
##### Body

#### I2C Endpoint
##### URI Format
```
deviceio-i2c://(0x)busId/(0x)deviceId[?options]
```

##### URI Options
##### URI Options
##### Headers
##### Body

### Camel Tinkerforge component

::: the component documentation here :::
