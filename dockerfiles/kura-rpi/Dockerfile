FROM hypriot/rpi-java
MAINTAINER Greg AUTRIC <gautric@redhat.com>

## to build it
## sudo docker build -t rpi-kura .
## to run it
## sudo docker run -d rpi-kura

## Variable ENV
ENV JAVA_VERSION java7
ENV KURA_VERSION 1.3.0
ENV RPI_VERSION  raspberry-pi-bplus

## Debian/Raspbian package installation
RUN apt-get update && \
    apt-get install -y apt-utils unzip ethtool dos2unix telnet bind9 hostapd isc-dhcp-server iw monit wget openjdk-7-jdk && \
    rm -rf /var/lib/apt/lists/*

## Kura installation
RUN wget https://s3.amazonaws.com/kura_downloads/raspbian/release/${KURA_VERSION}/kura_${KURA_VERSION}_${RPI_VERSION}_installer.deb
RUN dpkg -i kura_${KURA_VERSION}_${RPI_VERSION}_installer.deb

## Web and telnet
EXPOSE 80 5002

CMD /opt/eclipse/kura/bin/start_kura.sh
