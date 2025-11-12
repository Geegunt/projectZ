package ru.mai.topit.volunteers.platform.eventservice.domain;

public enum EventType {
    ONLINE("online"),
    OFFLINE("offline"),
    HYBRID("hybrid");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventType fromValue(String value) {
        for (EventType type : EventType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + value);
    }
}
