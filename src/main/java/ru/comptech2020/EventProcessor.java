package ru.comptech2020;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import ru.comptech2020.events.Event;
import ru.comptech2020.middlewares.ParseEvent;
import ru.comptech2020.sinks.ElasticSink;
import ru.comptech2020.sources.KafkaConsumer;

import java.util.Optional;

public class EventProcessor {
    public static void main(String[] args) throws Exception {
        final ParameterTool fromArgs = ParameterTool.fromArgs(args);
        final String zookeeper = fromArgs.get("zookeeper", "localhost:2181");
        final String kafka = fromArgs.get("kafka", "localhost:9092");
        final String topic = fromArgs.get("topic", "events");
        final String groupId = fromArgs.get("group-id", "group_id");
        final String elastic = fromArgs.get("elastic", "localhost:9200");
        final String index = fromArgs.get("index", "events");
        final String eventType = fromArgs.get("event-type", "user_location");
        final int batchSize = fromArgs.getInt("batch-size", 15_000);
        final long flushInterval = fromArgs.getLong("flush-interval", 1000L);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final SourceFunction<String> kafkaConsumer = KafkaConsumer.of(zookeeper, kafka, topic, groupId);
        final MapFunction<String, Optional<Event>> parseEvent = new ParseEvent(eventType);
        final SinkFunction<Optional<Event>> elasticSink = ElasticSink.of(elastic, index, batchSize, flushInterval);

        env.addSource(kafkaConsumer).map(parseEvent).addSink(elasticSink);
        env.execute();
    }
}
