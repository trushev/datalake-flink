#!/bin/bash

"$1"/bin/zookeeper-server-start.sh "$1"/config/zookeeper.properties & "$1"/bin/kafka-server-start.sh "$1"/config/server.properties
