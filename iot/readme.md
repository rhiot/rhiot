# Camel IoT Labs

<a href="https://github.com/camel-labs/camel-labs"><img src="../camel-labs.png" align="left" height="80" hspace="30"></a>
Camel IoT Labs project covers modules providing the Internet Of Things functionalities related to the 
[Apache Camel](http://camel.apache.org).
<br><br>

## Camel IoT components

Camel IoT Labs brings some extra components for the Apache Camel intended to make both device- and server-side IoT
development easier.

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
