# © 2019 Nokia
#
# Licensed under the BSD 3 Clause license
# SPDX-License-Identifier: BSD-3-Clause

FROM openjdk:8 AS build

RUN mkdir -p /opt/tsdc/daemon\
             /root/.m2

COPY src /opt/tsdc/daemon/src
COPY pom.xml /opt/tsdc/daemon

RUN chmod -R 755 /opt/tsdc/ &&\
    apt update &&\
    apt install -y maven

WORKDIR /opt/tsdc/daemon

RUN mvn package -DSkipTest

##

FROM openjdk:8

WORKDIR /opt/tsdc/daemon

COPY --from=build /opt/tsdc/daemon/target/time-series-data-collector.jar .
COPY resources /opt/tsdc/daemon/resources

EXPOSE 9995

ENTRYPOINT ["java", "-jar", "time-series-data-collector.jar"]
