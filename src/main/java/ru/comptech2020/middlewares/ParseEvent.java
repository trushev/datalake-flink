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

    @Override
    public Optional<Event> map(String value) {
        try {
//            switch (EVENT_TYPE) {
//                case ("user_location"):
//                    return Optional.of(new UserLocation(element));
//                default:
//                    throw new IllegalArgumentException("Illegal event type: " + EVENT_TYPE);
//            }
            return Optional.of(new UserLocation(value));

        } catch (EventParseException e) {
            LOGGER.error(e.getMessage());
            return Optional.empty();
        }
    }
}
