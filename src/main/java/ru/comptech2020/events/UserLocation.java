package ru.comptech2020.events;

import ru.comptech2020.exceptions.EventParseException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UserLocation implements Event {
    private String ctn;
    private double lat;
    private double lon;
    private String timestamp;

    public UserLocation(String csv) {
        final String[] fields = csv.split(",");
        if (fields.length != 4) {
            throw new EventParseException("Invalid number of fields in record: " + csv);
        }
        ctn = fields[0];
        lat = parseGeo(fields[1], -90, 90);
        lon = parseGeo(fields[2], -180, 180);
        timestamp = parseTimestamp(fields[3]);
    }

    @Override
    public Map<String, Object> toJson() {
        final HashMap<String, Double> location = new HashMap<>();
        location.put("lat", lat);
        location.put("lon", lon);

        final Map<String, Object> json = new HashMap<>();
        json.put("ctn", ctn);
        json.put("location", location);
        json.put("timestamp", timestamp);
        return Collections.unmodifiableMap(json);
    }

    private String parseTimestamp(String string) {
        final long milliseconds;
        try {
            milliseconds = Long.parseLong(string);
        } catch (NumberFormatException e) {
            throw new EventParseException("Invalid timestamp: " + string);
        }
        final Instant instant = Instant.ofEpochMilli(milliseconds);
        // timezone?
        final ZoneId zoneId = ZoneId.systemDefault();
        final LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime.toString();
    }

    private double parseGeo(String string, double min, double max) {
        final double geo;
        try {
            geo = Double.parseDouble(string);
        } catch (NumberFormatException e) {
            throw new EventParseException("Invalid geo coordinate: " + string);
        }
        if (geo < min) {
            return min;
        }
        return Math.min(geo, max);
    }
}
