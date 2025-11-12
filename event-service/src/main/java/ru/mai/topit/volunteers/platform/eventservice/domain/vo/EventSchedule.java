package ru.mai.topit.volunteers.platform.eventservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.OffsetDateTime;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventSchedule {
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private OffsetDateTime registrationDeadline;

    public boolean isValid() {
        if (startDate == null || endDate == null) {
            return false;
        }
        if (startDate.isAfter(endDate)) {
            return false;
        }
        if (registrationDeadline != null && registrationDeadline.isAfter(startDate)) {
            return false;
        }
        return true;
    }

    public boolean isEventStarted() {
        return OffsetDateTime.now().isAfter(startDate);
    }

    public boolean isEventEnded() {
        return OffsetDateTime.now().isAfter(endDate);
    }

    public boolean isRegistrationOpen() {
        if (registrationDeadline == null) {
            return !isEventStarted();
        }
        OffsetDateTime now = OffsetDateTime.now();
        return now.isBefore(registrationDeadline) && !isEventStarted();
    }

    public long getDurationMinutes() {
        return java.time.temporal.ChronoUnit.MINUTES.between(startDate, endDate);
    }
}
