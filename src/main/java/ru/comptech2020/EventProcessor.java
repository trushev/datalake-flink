package ru.comptech2020;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.comptech2020.events.Event;
import ru.comptech2020.events.UserLocation;
import ru.comptech2020.exceptions.EventParseException;

import java.util.*;

public class EventProcessor {
    private static final String ZOOKEEPER = "192.168.1.173:2181";
    private static final String KAFKA_SERVER = "192.168.1.173:9092";
    // private static final String ZOOKEEPER = "localhost:2181";
    // private static final String KAFKA_SERVER = "localhost:9092";
    private static final String TOPIC = "events";
    private static final String GROUP_ID = "group_id";
    private static final String ELASTIC_SERVER = "https://afeff8847d7040998ce840f91a916ce2.us-east-1.aws.found.io:9243";
    // private static final String ELASTIC_SERVER = "localhost:9200";
    private static final String ELASTIC_INDEX = "events";

    private static FlinkKafkaConsumer011<String> createKafkaConsumer() {
        final Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", KAFKA_SERVER);
        kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER);
        kafkaProps.setProperty("group.id", GROUP_ID);
        return new FlinkKafkaConsumer011<>(
                TOPIC,
                new SimpleStringSchema(),
                kafkaProps
        );
    }

    private static ElasticsearchSink<String> createElasticSink() {
        final List<HttpHost> elasticHosts = new ArrayList<>();
        elasticHosts.add(HttpHost.create(ELASTIC_SERVER));
        final ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
                elasticHosts,
                (element, ctx, indexer) -> {
                    final Event userLocation;
                    try {
                        userLocation = new UserLocation(element);
                    } catch (EventParseException e) {
                        LOGGER.error(e.getMessage());
                        return;
                    }
                    indexer.add(
                        Requests.indexRequest()
                                .index(ELASTIC_INDEX)
                                .type("_doc")
                                .source(userLocation.toJson())
                    );
                }
        );
        esSinkBuilder.setBulkFlushMaxActions(1);
        return esSinkBuilder.build();
    }

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final FlinkKafkaConsumer011<String> kafkaConsumer = createKafkaConsumer();
        final ElasticsearchSink<String> elasticSink = createElasticSink();
        env.addSource(kafkaConsumer).addSink(elasticSink);
        env.execute();
    }
}
