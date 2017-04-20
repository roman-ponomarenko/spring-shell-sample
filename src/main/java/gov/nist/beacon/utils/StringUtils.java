package gov.nist.beacon.utils;

import org.springframework.shell.support.util.OsUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.countOccurrencesOf;

public class StringUtils {
    private StringUtils() {
    }

    public static String getBeaconSummary(String outputValue) {
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
