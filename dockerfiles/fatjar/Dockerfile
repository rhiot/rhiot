FROM java:8u40

MAINTAINER hekonsek@gmail.com

RUN mkdir /jars
ADD fatjar.sh /opt/
RUN chmod +x /opt/fatjar.sh

ENTRYPOINT ["/opt/fatjar.sh"]
