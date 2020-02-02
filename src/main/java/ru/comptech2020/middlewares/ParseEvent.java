package ru.comptech2020.middlewares;

import org.apache.flink.api.common.functions.MapFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.comptech2020.events.Event;
import ru.comptech2020.events.UserLocation;
import ru.comptech2020.exceptions.EventParseException;

import java.util.Optional;

public class ParseEvent implements MapFunction<String, Optional<Event>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseEvent.class);

    private final String eventType;
    public ParseEvent(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public Optional<Event> map(String value) {
        try {
            if ("user_location".equals(eventType)) {
                return Optional.of(new UserLocation(value));
            }
            throw new IllegalArgumentException("Illegal event type: " + eventType);
        } catch (EventParseException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }
}
