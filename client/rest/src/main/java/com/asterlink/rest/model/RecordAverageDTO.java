package com.asterlink.rest.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * DTO for sending average data
 * @author gl3bert
 */

public record RecordAverageDTO(LocalDateTime section, double value) {
    public static RecordAverageDTO from(Object[] result) {
        // Safely extract timestamp
        LocalDateTime section = (result[0] instanceof Timestamp ts) ? ts.toLocalDateTime() : null;

        // Safely extract value (defaulting to 0.0 if null)
        double value = (result[1] instanceof Number num) ? Math.round(num.doubleValue() * 10.0) / 10.0 : 0.0;

        return new RecordAverageDTO(section, value);
    }
}
