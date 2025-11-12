package ru.mai.topit.volunteers.platform.eventservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ParticipantLimits {
    private Integer maxParticipants;
    private Integer currentParticipants;

    public boolean isValid() {
        if (currentParticipants == null || currentParticipants < 0) {
            return false;
        }
        if (maxParticipants != null && maxParticipants <= 0) {
            return false;
        }
        if (maxParticipants != null && currentParticipants > maxParticipants) {
            return false;
        }
        return true;
    }

    public boolean hasAvailableSlots() {
        if (maxParticipants == null) {
            return true;
        }
        return currentParticipants < maxParticipants;
    }

    public int getAvailableSlots() {
        if (maxParticipants == null) {
            return Integer.MAX_VALUE;
        }
        return maxParticipants - currentParticipants;
    }

    public ParticipantLimits incrementParticipants() {
        if (!hasAvailableSlots()) {
            throw new IllegalStateException("No available slots for new participants");
        }
        return ParticipantLimits.builder()
                .maxParticipants(this.maxParticipants)
                .currentParticipants(this.currentParticipants + 1)
                .build();
    }

    public ParticipantLimits decrementParticipants() {
        if (currentParticipants == 0) {
            throw new IllegalStateException("Current participants cannot be less than 0");
        }
        return ParticipantLimits.builder()
                .maxParticipants(this.maxParticipants)
                .currentParticipants(this.currentParticipants - 1)
                .build();
    }
}
