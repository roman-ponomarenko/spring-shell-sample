package gov.nist.beacon;

import gov.nist.beacon.clients.RestClient;
import gov.nist.beacon.entities.BeaconResponse;
import gov.nist.beacon.entities.TimeOptions;
import gov.nist.beacon.utils.StringUtils;
import gov.nist.beacon.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import static gov.nist.beacon.utils.WaitUtils.tryTo;

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
        return StringUtils.getBeaconSummary(resp.getBody().getOutputValue());
    }

    public String distinguish(TimeOptions from, TimeOptions to) {
        long fromTime = TimeUtils.getEpochTimeInSeconds(from);
        long toTime = TimeUtils.getEpochTimeInSeconds(to);
        StringBuilder sb = new StringBuilder();

        ResponseEntity<BeaconResponse> resp;
        resp = tryTo(duration, interval, () -> client.getForObject(currentBeaconUrl, BeaconResponse.class, fromTime));
        sb.append(resp.getBody().getOutputValue());

        while (resp.getBody().getTimeStamp() < toTime) {
            long next = resp.getBody().getTimeStamp();
            resp = tryTo(duration, interval, () -> client.getForObject(nextBeaconUrl, BeaconResponse.class, next));
            sb.append(resp.getBody().getOutputValue());
        }

        return StringUtils.getBeaconSummary(sb.toString());
    }
}
