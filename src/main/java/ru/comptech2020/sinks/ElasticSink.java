package ru.comptech2020.sinks;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Requests;
import ru.comptech2020.events.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ElasticSink {
    public static SinkFunction<Optional<Event>> of(String server, String index) {
        final List<HttpHost> elasticHosts = new ArrayList<>();
        elasticHosts.add(HttpHost.create(server));
        final ElasticsearchSink.Builder<Optional<Event>> esSinkBuilder = new ElasticsearchSink.Builder<>(
                elasticHosts,
                (event, ctx, indexer) -> event.ifPresent(e -> indexer.add(
                        Requests.indexRequest(index)
                                .type("_doc")
                                .source(e.toJson())
                        )
                )
        );
        esSinkBuilder.setBulkFlushMaxActions(1);
        return esSinkBuilder.build();
    }
}
