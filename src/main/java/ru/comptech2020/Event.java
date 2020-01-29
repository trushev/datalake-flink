package ru.comptech2020;

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
        lat = Double.parseDouble(fields[1]);
        lon = Double.parseDouble(fields[2]);
        timestamp = fields[3];
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
}
