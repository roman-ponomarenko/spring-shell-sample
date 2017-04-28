package gov.nist.beacon;

import gov.nist.beacon.entities.TimeOptions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EpochTimeTests extends BaseTests {
    @Autowired
    private BeaconSummarizer summarizer;

    @Test
    public void currentTimeTest() {
        long actualTime = summarizer.getEpochTimeInSeconds(TimeOptions.of(0, 0, 0, 0));
        long currentTime = System.currentTimeMillis() / 1000L;
        assertThat((currentTime / 60) - (actualTime / 60), is(0L));
    }

    @Test
    public void minutesTest() {
        long utilTimeEpoch = summarizer.getEpochTimeInSeconds(TimeOptions.of(0, 0, 0, 7));
        long currentTimeEpoch = System.currentTimeMillis() / 1000L;
        LocalDateTime utilTime = Instant.ofEpochSecond(utilTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentTime = Instant.ofEpochSecond(currentTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(currentTime.getMinute(), is(utilTime.getMinute() + 7));
    }

    @Test
    public void hoursTest() {
        long utilTimeEpoch = summarizer.getEpochTimeInSeconds(TimeOptions.of(0, 0, 2, 0));
        long currentTimeEpoch = System.currentTimeMillis() / 1000L;
        LocalDateTime utilTime = Instant.ofEpochSecond(utilTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentTime = Instant.ofEpochSecond(currentTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(currentTime.getHour(), is(utilTime.getHour() + 2));
    }

    @Test
    public void daysTest() {
        long utilTimeEpoch = summarizer.getEpochTimeInSeconds(TimeOptions.of(0, 30, 0, 0));
        long currentTimeEpoch = System.currentTimeMillis() / 1000L;
        LocalDateTime utilTime = Instant.ofEpochSecond(utilTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentTime = Instant.ofEpochSecond(currentTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(currentTime.getDayOfYear(), is(utilTime.getDayOfYear() + 30));
    }

    @Test
    public void monthTest() {
        long utilTimeEpoch = summarizer.getEpochTimeInSeconds(TimeOptions.of(2, 0, 0, 0));
        long currentTimeEpoch = System.currentTimeMillis() / 1000L;
        LocalDateTime utilTime = Instant.ofEpochSecond(utilTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentTime = Instant.ofEpochSecond(currentTimeEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();

        assertThat(currentTime.getMonthValue(), is(utilTime.getMonthValue() + 2));
    }
}