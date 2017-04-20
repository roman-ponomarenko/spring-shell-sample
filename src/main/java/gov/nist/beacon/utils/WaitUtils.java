package gov.nist.beacon.utils;

import gov.nist.beacon.exception.BeaconServiceUnavailableException;
import ru.stqa.trier.LimitExceededException;
import ru.stqa.trier.TimeBasedTrier;

import java.util.function.Supplier;

public class WaitUtils {

    private WaitUtils() {
    }

    @SuppressWarnings("squid:S2142")
    public static void delay(int timeOutInMs) {
        try {
            Thread.sleep(timeOutInMs);
        } catch (InterruptedException e) {
            //ignore
        }
    }

    @SuppressWarnings("squid:S1166")
    public static <T> T tryTo(long duration, long interval, Supplier<T> s) {
        try {
            return new TimeBasedTrier<>(duration, interval).tryTo(s);
        } catch (LimitExceededException | InterruptedException e) {
            throw new BeaconServiceUnavailableException("Unable to get response from Beacon service during 30 seconds.");
        }
    }
}
