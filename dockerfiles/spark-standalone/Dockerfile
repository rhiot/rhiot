#  Licensed to the Rhiot under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

FROM java:8u66

MAINTAINER Henryk Konsek <hekonsek@gmail.com>

ENV SPARK_DIST spark-1.5.1-bin-hadoop2.6
RUN curl -s http://d3kbcqa49mib13.cloudfront.net/${SPARK_DIST}.tgz | tar -xz -C /opt/
RUN cd /opt && ln -s ${SPARK_DIST} spark

ADD scripts/start-master.sh /start-master.sh
ADD scripts/start-worker.sh /start-worker.sh
ADD scripts/spark-submit.sh /spark-submit.sh
ADD scripts/spark-defaults.conf /spark-defaults.conf
ADD scripts/remove_alias.sh /remove_alias.sh

ENV SPARK_HOME /opt/spark

RUN cp $SPARK_HOME/conf/spark-env.sh.template $SPARK_HOME/conf/spark-env.sh 
RUN /bin/echo "export SPARK_WORKER_INSTANCES=2" >> $SPARK_HOME/conf/spark-env.sh
RUN ln -s $SPARK_HOME/sbin/start-slaves.sh /start-slaves.sh
RUN ln -s $SPARK_HOME/sbin/start-slave.sh /start-slave.sh

ENV SPARK_MASTER_OPTS "-Dspark.driver.port=7001 -Dspark.fileserver.port=7002 -Dspark.broadcast.port=7003 -Dspark.replClassServer.port=7004 -Dspark.blockManager.port=7005 -Dspark.executor.port=7006 -Dspark.ui.port=4040 -Dspark.broadcast.factory=org.apache.spark.broadcast.HttpBroadcastFactory"
ENV SPARK_WORKER_OPTS "-Dspark.driver.port=7001 -Dspark.fileserver.port=7002 -Dspark.broadcast.port=7003 -Dspark.replClassServer.port=7004 -Dspark.blockManager.port=7005 -Dspark.executor.port=7006 -Dspark.ui.port=4040 -Dspark.broadcast.factory=org.apache.spark.broadcast.HttpBroadcastFactory"

ENV SPARK_MASTER_PORT 7077
ENV SPARK_MASTER_WEBUI_PORT 8080
ENV SPARK_WORKER_PORT 8888
ENV SPARK_WORKER_WEBUI_PORT 9091

EXPOSE 8080 7077 8888 9091 4040 7001 7002 7003 7004 7005 7006