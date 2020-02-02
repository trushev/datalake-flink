package ru.comptech2020.middlewares;

import org.junit.jupiter.api.Test;
import ru.comptech2020.events.UserLocation;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseEventTest {
    @Test
    void map_UserLocationAsCsv_PresentOptionalOfUserLocation() {
        final String input = "79993002010,54.84296,83.091047,1580632599";
        final Optional<UserLocation> expected = Optional.of(new UserLocation(
                "79993002010", 54.84296, 83.091047, "1970-01-19T07:03:52.599"
        ));
        final ParseEvent parseEvent = new ParseEvent("user_location");
        assertEquals(expected, parseEvent.map(input));
    }
}
