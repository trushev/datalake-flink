package ru.comptech2020.sources;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.util.Properties;

public class KafkaConsumer {
    public static SourceFunction<String> of(String zookeeper, String kafka, String topic, String groupId) {
        final Properties kafkaProps = new Properties();
        kafkaProps.setProperty("zookeeper.connect", zookeeper);
        kafkaProps.setProperty("bootstrap.servers", kafka);
        kafkaProps.setProperty("group.id", groupId);
        return new FlinkKafkaConsumer011<>(
                topic,
                new SimpleStringSchema(),
                kafkaProps
        );
    }
}
