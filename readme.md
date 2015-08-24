# Rhiot - the messaging platform for the Internet Of Things

<a href="https://github.com/rhiot/rhiot"><img src="rhiot.png" align="left" height="250" hspace="30"></a>
Rhiot is the messaging platform for the Internet Of Things. We are focused on the adoption of the
[Red Hat JBoss middleware portfolio](http://www.redhat.com/en/technologies/jboss-middleware) to provide the solutions to
the common IoT-related challenges.

Rhiot comes with the following features:
- [IoT gateway software](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-iot-gateway)
- [Camel components for the IoT](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#camel-iot-components)
- [Backend cloud services (Cloudlets)](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#cloudlets)
- Web console for managing the devices, gateways and Cloudlets
- IoT deployment utilities
- [Performance Testing Framework for the IoT gateways](https://github.com/rhiot/rhiot/blob/master/docs/readme.md#performance-testing-framework)

## Documentation

[Here](https://github.com/rhiot/rhiot/blob/master/docs/readme.md) you can find the reference documentation for the Rhiot.

## Current release

The latest and the greatest version of the Rhiot is 0.1.0. Wondering what's new there? Here are the [release notes](https://github.com/rhiot/rhiot/blob/master/release-notes.md).

## Mailing list

You're more than welcome to join out [ Rhiot Labs mailing list](https://groups.google.com/forum/#!forum/camel-iot-labs).

## Issues

You can create [new issue, bug, comment here](https://github.com/rhiot/rhiot/issues/new).

## Building the project

All you need to build the project is Maven 3 and Java 8 JDK

### Developer host

    git clone https://github.com/rhiot/rhiot.git
    cd rhiot
    mvn install

### Via Docker

####1st step (once)

    git clone https://github.com/rhiot/rhiot.git;
    cd rhiot;
    docker build -t rhiot build


####2nd step

    cd rhiot;
    docker run -v `pwd`:/rhiot --privileged -i -t rhiot


## Build status

[![Build Status](https://travis-ci.org/rhiot/rhiot.svg?branch=master)](https://travis-ci.org/rhiot/rhiot)

## License

The Rhiot project is distributed under the [Apache 2.0 Software License](https://www.apache.org/licenses/LICENSE-2.0).

## The Team

| Name | Contact | URL | Company | Spoken Lang |
|------|---------|-----|----------|-------------|
| Henryk K. | [@hekonsek](https://twitter.com/hekonsek) | https://about.me/hekonsek | Red Hat | PL (native), EN |
| Greg A. | [@gautric_io](https://twitter.com/gautric_io) | http://gautric.github.io/ | Red Hat | FR (native), EN, JP (beginner) |
| Geert S. | [@geertschuring](https://twitter.com/geertschuring) | https://geertschuring.wordpress.com/ | Geert Schuring | NL (native), EN |
