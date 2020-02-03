## Данный проект содержит код для Apache Flink

*Для запуска процессора не через докер необходимо запустить*
1. Zookeeper
2. Kafka
```
wget https://archive.apache.org/dist/kafka/0.11.0.3/kafka_2.11-0.11.0.3.tgz
tar -xzf kafka_2.11-0.11.0.3.tgz
cd kafka_2.11-0.11.0.3
./bin/zookeeper-server-start.sh config/zookeeper.properties
./bin/kafka-server-start.sh config/server.properties
```
3. Создать топик
```
./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic events
```
4. Скачать и запустить образы ES и Kibana
```
docker pull elasticsearch:7.5.2
docker pull kibana:7.5.2
docker network create mynetwork
docker run -d --name elasticsearch --net mynetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.5.2
docker run -d --name kibana --net mynetwork -p 5601:5601 kibana:7.5.2
```
5. Создать индекс в ES
```
curl -X PUT "localhost:9200/events?pretty" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "ctn": { "type": "text" },
      "location": { "type": "geo_point" },
      "timestamp": { "type": "date" }
    }
  }
}
'
```


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
