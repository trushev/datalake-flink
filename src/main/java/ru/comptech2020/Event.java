package ru.comptech2020;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Event {
    private String ctn;
    private double lat;
    private double lon;
    private String timestamp;

    public Event(String csv) {
        final String[] fields = csv.split(",");
        if (fields.length != 4) {
            throw new RuntimeException("Invalid csv record: " + csv);
        }
        ctn = fields[0];
        lat = parseGeo(fields[1], -90, 90);
        lon = parseGeo(fields[2], -180, 180);
        timestamp = parseTimestamp(fields[3]);
    }

    public String getCtn() {
        return ctn;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + ctn + '\'' +
                ", lan=" + lat +
                ", log=" + lon +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    private String parseTimestamp(String string) {
        final long l = Long.parseLong(string);
        final Instant instant = Instant.ofEpochMilli(l);
        // timezone?
        final LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        return localDateTime.toString();
    }

    private double parseGeo(String string, double min, double max) {
        final double geo = Double.parseDouble(string);
        if (geo < min) {
            return min;
        }
        return Math.min(geo, max);
    }
}
