package ru.mai.topit.volunteers.platform.eventservice.domain;

public enum EventApplicationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELLED("cancelled");

    private final String value;

    EventApplicationStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static EventApplicationStatus fromValue(String value) {
        for (EventApplicationStatus status : EventApplicationStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown event application status: " + value);
    }
}
