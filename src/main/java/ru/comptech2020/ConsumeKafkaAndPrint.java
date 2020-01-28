package ru.comptech2020;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;


/**
 * Достает сообщения из топика events и выводит в stdout
 *
 * Перед запуском main нужно выполнить:
 * cd kafka_2.11-0.11.0.3
 * ./bin/zookeeper-server-start.sh config/zookeeper.properties
 * ./bin/kafka-server-start.sh config/server.properties
 * ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic events
 *
 * После запуска main:
 * ./bin/kafka-console-producer.sh --broker-list localhost:9092 --topic events
 * Hello
 */
public class ConsumeKafkaAndPrint {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final String topicName = "events";
        final String kafkaServer = "localhost:9092";
        final String zookeeper = "localhost:2181";
        final String groupId = "group_id";
        final Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", kafkaServer);
        kafkaProps.setProperty("zookeeper.connect", zookeeper);
        kafkaProps.setProperty("group.id", groupId);
        final FlinkKafkaConsumer011<String> eventsConsumer = new FlinkKafkaConsumer011<>(
                topicName,
                new SimpleStringSchema(),
                kafkaProps
        );
        final DataStream<String> eventStreamString = env
                .addSource(eventsConsumer)
                .name("Event Consumer");
        eventStreamString.print();
        env.execute();
    }
}
