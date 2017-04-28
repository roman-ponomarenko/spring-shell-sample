package gov.nist.beacon;

import gov.nist.beacon.clients.RestClient;
import gov.nist.beacon.entities.BeaconResponse;
import gov.nist.beacon.entities.TimeOptions;
import gov.nist.beacon.exception.BeaconServiceUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;
import ru.stqa.trier.LimitExceededException;
import ru.stqa.trier.TimeBasedTrier;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.countOccurrencesOf;

@Component
public class BeaconSummarizer {
    @Autowired
    private RestClient client;

    @Value("${beacon.current.url}")
    private String currentBeaconUrl;

    @Value("${beacon.next.url}")
    private String nextBeaconUrl;

    @Value("${beacon.last.url}")
    private String lastBeaconUrl;

    private long duration = 30_000;

    private long interval = 1_000;

    public String distinguish() {
        ResponseEntity<BeaconResponse> resp = tryTo(duration, interval,
                () -> client.getForObject(lastBeaconUrl, BeaconResponse.class));
        return getBeaconSummary(resp.getBody().getOutputValue());
    }

    public String distinguish(TimeOptions from, TimeOptions to) {
        long fromTime = getEpochTimeInSeconds(from);
        long toTime = getEpochTimeInSeconds(to);
        StringBuilder sb = new StringBuilder();

        ResponseEntity<BeaconResponse> resp;
        resp = tryTo(duration, interval, () -> client.getForObject(currentBeaconUrl, BeaconResponse.class, fromTime));
        sb.append(resp.getBody().getOutputValue());

        while (resp.getBody().getTimeStamp() < toTime) {
            long next = resp.getBody().getTimeStamp();
            resp = tryTo(duration, interval, () -> client.getForObject(nextBeaconUrl, BeaconResponse.class, next));
            sb.append(resp.getBody().getOutputValue());
        }

        return getBeaconSummary(sb.toString());
    }

    @SuppressWarnings("squid:S1166")
    private <T> T tryTo(long duration, long interval, Supplier<T> s) {
        try {
            return new TimeBasedTrier<>(duration, interval).tryTo(s);
        } catch (LimitExceededException | InterruptedException e) {
            throw new BeaconServiceUnavailableException("Unable to get response from Beacon service during 30 seconds.");
        }
    }

    long getEpochTimeInSeconds(TimeOptions options) {
        LocalDateTime timePoint = LocalDateTime.now().with(LocalTime.now().truncatedTo(ChronoUnit.MINUTES));
        timePoint = timePoint.minusMonths(options.getMonth());
        timePoint = timePoint.minusDays(options.getDays());
        timePoint = timePoint.minusHours(options.getHours());
        timePoint = timePoint.minusMinutes(options.getMinutes());
        return timePoint.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000L;
    }

    String getBeaconSummary(String outputValue) {
        Map<String, Integer> sum = new HashMap<>();

        outputValue.chars()
                .mapToObj(ch -> (char) ch)
                .distinct()
                .forEach(ch -> sum.put(ch.toString(), countOccurrencesOf(outputValue, ch.toString())));

        return sum.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "," + entry.getValue())
                .collect(Collectors.joining(OsUtils.LINE_SEPARATOR));
    }
}
