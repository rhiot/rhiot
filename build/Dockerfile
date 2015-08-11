FROM centos

MAINTAINER Greg Autric <gautric@redhat.com> (@gautric_io)

# From official Dockerfile
# Assemble the full dev environment. This is slow the first time.
# docker build -t rhiot build
#
# # Mount your source in an interactive container for quick testing:
# docker run -v `pwd`:/rhiot --privileged -i -t rhiot

RUN yum -y install java-1.8.0-openjdk-devel git maven

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk
ENV RHIOT_SRC_DIR /rhiot

CMD cd $RHIOT_SRC_DIR; mvn clean package
