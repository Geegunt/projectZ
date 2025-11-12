package ru.mai.topit.volunteers.platform.eventservice.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Location {
    private String name;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public boolean isValid() {
        if ((latitude != null || longitude != null) && (latitude == null || longitude == null)) {
            return false;
        }
        if (latitude != null && longitude != null) {
            return latitude.compareTo(new BigDecimal("-90")) >= 0 &&
                    latitude.compareTo(new BigDecimal("90")) <= 0 &&
                    longitude.compareTo(new BigDecimal("-180")) >= 0 &&
                    longitude.compareTo(new BigDecimal("180")) <= 0;
        }
        return true;
    }
}
