package ru.comptech2020;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import ru.comptech2020.events.Event;
import ru.comptech2020.middlewares.ParseEvent;
import ru.comptech2020.sinks.ElasticSink;
import ru.comptech2020.sources.KafkaConsumer;

import java.util.Optional;

public class EventProcessor {
    private static final String ZOOKEEPER = "localhost:2181";
    private static final String KAFKA_SERVER = "localhost:9092";
    private static final String TOPIC = "events";
    private static final String GROUP_ID = "group_id";
    private static final String ELASTIC_SERVER = "localhost:9200";
    private static final String ELASTIC_INDEX = "events";
//    private static final String EVENT_TYPE = "user_location";

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final SourceFunction<String> kafkaConsumer = KafkaConsumer.of(ZOOKEEPER, KAFKA_SERVER, TOPIC, GROUP_ID);
        final MapFunction<String, Optional<Event>> parseEvent = new ParseEvent();
        final SinkFunction<Optional<Event>> elasticSink = ElasticSink.of(ELASTIC_SERVER, ELASTIC_INDEX);
        env.addSource(kafkaConsumer).map(parseEvent).addSink(elasticSink);
        env.execute();
    }
}
