package ru.comptech2020.events;

import java.util.Map;

public interface Event {
    Map<String, Object> toJson();
}
