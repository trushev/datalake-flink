package ru.comptech2020.events;

import ru.comptech2020.exceptions.EventParseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserLocation implements Event {
    private static final ZoneId ZONE_ID = ZoneId.of("UTC");
    private static final String CSV_DELIMITER = ",";

    private String ctn;
    private double lat;
    private double lon;
    private String timestamp;

    public UserLocation(String csv) {
        final String[] fields = csv.split(CSV_DELIMITER);
        if (fields.length != 4) {
            throw new EventParseException("Illegal number of fields in record: " + csv);
        }
        ctn = fields[0];
        lat = parseGeo(fields[1], -90, 90);
        lon = parseGeo(fields[2], -180, 180);
        timestamp = parseTimestamp(fields[3]);
    }

    @Override
    public Map<String, Object> toJson() {
        final Map<String, Object> json = new HashMap<>();
        json.put("ctn", ctn);
        json.put("location", String.format("%s,%s", lat, lon));
        json.put("timestamp", timestamp);
        return Collections.unmodifiableMap(json);
    }

    private String parseTimestamp(String string) {
        final long milliseconds;
        try {
            milliseconds = Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new EventParseException("Illegal timestamp: " + string);
        }
        final Instant instant = Instant.ofEpochMilli(milliseconds);
        final LocalDateTime localDateTime = instant.atZone(ZONE_ID).toLocalDateTime();
        return localDateTime.toString();
    }

    private double parseGeo(String string, double min, double max) {
        final double geo;
        try {
            geo = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new EventParseException("Illegal geo coordinate: " + string);
        }
        if (geo < min || geo > max) {
            throw new EventParseException(String.format(
                    "Illegal geo coordinate: %s, supported range: [%s, %s]", geo, min, max
            ));
        }
        return geo;
    }
}
