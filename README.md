## Данный проект содержит код для Apache Flink

*Запуск проекта*

```
mvn clean package
mvn exec:java -Dexec.mainClass=ru.comptech2020.EventProcessor
```

*Запуск проекта в докере*
1. Перейти в корень проекта
2. Выполнить команды

```
docker build . -t datalake-flink
docker run --name app -d datalake-flink
```

*Запуск во Flink кластере*

```
mvn clean package
~/flink-1.9.0/bin/start-cluster.sh
~/flink-1.9.0/bin/flink run target/datalake-flink-1.0.0.jar
```

*Запуск с параметрами*
```
~/flink-1.9.0/bin/flink run target/datalake-flink-1.0.0.jar \
-zookeeper localhost:2181 \
-kafka localhost:9092 \
-topic events \
-group-id group_id \
-elastic localhost:9200 \
-index events \
-event-type user_location \
-batch-size 15000 \
-flush-interval 1000
```
