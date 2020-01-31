#!/bin/bash
dir=kafka_2.11-0.11.0.3
$dir/bin/zookeeper-server-start.sh $dir/config/zookeeper.properties & $dir/bin/kafka-server-start.sh $dir/config/server.properties
