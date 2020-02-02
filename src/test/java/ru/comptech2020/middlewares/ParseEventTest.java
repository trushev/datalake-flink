package ru.comptech2020.middlewares;

import org.junit.Test;
import ru.comptech2020.events.UserLocation;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class ParseEventTest {
    @Test
    public void map_UserLocationAsCsv_PresentOptionalOfUserLocation() {
        final String input = "79993002010,54.84296,83.091047,1580632599000";
        final Optional<UserLocation> expected = Optional.of(new UserLocation(
                "79993002010", 54.84296, 83.091047, "2020-02-02T08:36:39"
        ));
        final ParseEvent parseEvent = new ParseEvent("user_location");
        assertEquals(expected, parseEvent.map(input));
    }
}
