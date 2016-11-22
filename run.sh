#!/bin/bash
reset
mvn exec:java -Dlog4j.configurationFile=/home/remerson/workspace/RedHat/infinispan/infinispan-jdbc-benchmark/src/main/resources/log4j.xml -Djava.net.preferIPv4Stack=true
