package ru.comptech2020.exceptions;

public class EventParseException extends RuntimeException {

    public EventParseException(String message) {
        super(message, null, false, false);
    }
}
