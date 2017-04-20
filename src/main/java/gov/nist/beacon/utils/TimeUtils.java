package gov.nist.beacon.utils;

import gov.nist.beacon.entities.TimeOptions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    private TimeUtils() {
    }

    public static long getEpochTimeInSeconds(TimeOptions options) {
        LocalDateTime timePoint = LocalDateTime.now().with(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        timePoint = timePoint.minusMonths(options.getMonth());
        timePoint = timePoint.minusDays(options.getDays());
        timePoint = timePoint.minusHours(options.getHours());
        timePoint = timePoint.minusMinutes(options.getMinutes());
        return timePoint.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000L;
    }
}
