package ru.mai.topit.volunteers.platform.eventservice.domain;

public enum EventStatus {
    DRAFT("draft"),
    PUBLISHED("published"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventStatus fromValue(String value) {
        for (EventStatus status : EventStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown event status: " + value);
    }
}
