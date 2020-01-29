package ru.comptech2020;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.*;

public class EventProcessor {
    private static final String TOPIC = "events";
    private static final String KAFKA_SERVER = "localhost:9092";
    private static final String ZOOKEEPER = "localhost:2181";
    private static final String GROUP_ID = "group_id";

    private static Properties getKafkaProps() {
        final Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", KAFKA_SERVER);
        kafkaProps.setProperty("zookeeper.connect", ZOOKEEPER);
        kafkaProps.setProperty("group.id", GROUP_ID);
        return kafkaProps;
    }

    public static void main(String[] args) throws Exception {
        final List<HttpHost> httpHosts = new ArrayList<>();
        httpHosts.add(new HttpHost("localhost", 9200, "http"));

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final Properties kafkaProps = getKafkaProps();
        final FlinkKafkaConsumer011<String> eventsConsumer = new FlinkKafkaConsumer011<>(
                TOPIC,
                new SimpleStringSchema(),
                kafkaProps
        );
        final DataStream<String> events = env
                .addSource(eventsConsumer)
                .name("Event Consumer");

        final ElasticsearchSink.Builder<String> esSinkBuilder = new ElasticsearchSink.Builder<>(
                httpHosts,
                new ElasticsearchSinkFunction<String>() {
                    public IndexRequest createIndexRequest(String element) {
                        final Event event = new Event(element);
                        final HashMap<String, Double> location = new HashMap<>();
                        location.put("lat", event.getLat());
                        location.put("lon", event.getLon());

                        final Map<String, Object> json = new HashMap<>();
                        json.put("ctn", event.getCtn());
                        json.put("location", location);
                        json.put("timestamp", event.getTimestamp());

                        return Requests.indexRequest()
                                .index("events")
                                .type("_doc")
                                .source(json);
                    }

                    @Override
                    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
                        indexer.add(createIndexRequest(element));
                    }
                }
        );
        esSinkBuilder.setBulkFlushMaxActions(1);
        events.addSink(esSinkBuilder.build());
        env.execute();
    }
}
