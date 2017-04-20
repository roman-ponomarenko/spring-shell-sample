package gov.nist.beacon.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import gov.nist.beacon.BeaconSummarizer;
import gov.nist.beacon.entities.TimeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class BeaconServiceCommands implements CommandMarker {
    public static final String SUMMARIZE_BEACON = "summarize-beacon";

    private static final String FROM_ARGUMENT = "from";
    private static final String TO_ARGUMENT = "to";

    private static final String MONTH = "Month(s)";
    private static final String DAY = "Day(s)";
    private static final String HOUR = "Hour(s)";
    private static final String MINUTE = "Minute(s)";

    private static final Pattern MONTH_EXPRESSION = Pattern.compile("(-?[0-9]{0,10}) (months|month)");
    private static final Pattern DAY_EXPRESSION = Pattern.compile("(-?[0-9]{0,10}) (days|day)");
    private static final Pattern HOUR_EXPRESSION = Pattern.compile("(-?[0-9]{0,10}) (hours|hour)");
    private static final Pattern MINUTE_EXPRESSION = Pattern.compile("(-?[0-9]{0,10}) (minutes|minute)");

    @Autowired
    private BeaconSummarizer distinguisher;

    @CliAvailabilityIndicator({SUMMARIZE_BEACON})
    public boolean isSummarizeBeaconAvailable() {
        return true;
    }

    @CliCommand(value = SUMMARIZE_BEACON, help = "Prints summarized info for beacon(s)")
    public String summarize(
            @CliOption(key = {FROM_ARGUMENT}, help = "Start of a span for beacon values")
                    String from,

            @CliOption(key = {TO_ARGUMENT}, help = "End of a span for beacon valuess")
                    String to) {
        StringBuilder validationMessage = new StringBuilder();
        String result;

        if (!Strings.isNullOrEmpty(from)) {
            validationMessage.append(validate(FROM_ARGUMENT, from));
        }
        if (!Strings.isNullOrEmpty(to)) {
            if (validationMessage.length() != 0) {
                validationMessage.append(OsUtils.LINE_SEPARATOR);
            }
            validationMessage.append(validate(FROM_ARGUMENT, to));
        }

        if (!Strings.isNullOrEmpty(from) && !Strings.isNullOrEmpty(to)) {
            result = distinguisher.distinguish(getArgumentOptions(from), getArgumentOptions(to));
        } else if ((Strings.isNullOrEmpty(from) && !Strings.isNullOrEmpty(to))
                || (!Strings.isNullOrEmpty(from) && Strings.isNullOrEmpty(to))) {
            result = "Only one optional argument is specified.";
        } else {
            result = distinguisher.distinguish();
        }

        return !validationMessage.toString().isEmpty() ? validationMessage.toString() : result;
    }

    public TimeOptions getArgumentOptions(String argumentValue) {
        int monthsValue = getArgumentOptionValue(MONTH_EXPRESSION, argumentValue);
        int daysValue = getArgumentOptionValue(DAY_EXPRESSION, argumentValue);
        int hoursValue = getArgumentOptionValue(HOUR_EXPRESSION, argumentValue);
        int minutesValue = getArgumentOptionValue(MINUTE_EXPRESSION, argumentValue);
        return TimeOptions.of(monthsValue, daysValue, hoursValue, minutesValue);
    }

    public String validate(String argumentName, String argumentValue) {
        List<String> errors = Lists.newArrayList();

        boolean isMonthsFound = MONTH_EXPRESSION.matcher(argumentValue).find();
        boolean isDaysFound = DAY_EXPRESSION.matcher(argumentValue).find();
        boolean isHoursFound = HOUR_EXPRESSION.matcher(argumentValue).find();
        boolean isMinutesFound = MINUTE_EXPRESSION.matcher(argumentValue).find();

        int monthsValue = getArgumentOptionValue(MONTH_EXPRESSION, argumentValue);
        int daysValue = getArgumentOptionValue(DAY_EXPRESSION, argumentValue);
        int hoursValue = getArgumentOptionValue(HOUR_EXPRESSION, argumentValue);
        int minutesValue = getArgumentOptionValue(MINUTE_EXPRESSION, argumentValue);

        boolean hasValidOption = isMonthsFound || isDaysFound || isHoursFound || isMinutesFound;
        if (!hasValidOption) {
            errors.add(String.format("None of options is valid for argument --%s", argumentName));
        }

        errors.add(validateArgumentOptionValue(argumentName, MONTH, isMonthsFound, monthsValue));
        errors.add(validateArgumentOptionValue(argumentName, DAY, isDaysFound, daysValue));
        errors.add(validateArgumentOptionValue(argumentName, HOUR, isHoursFound, hoursValue));
        errors.add(validateArgumentOptionValue(argumentName, MINUTE, isMinutesFound, minutesValue));

        return errors.stream().filter(p -> !Strings.isNullOrEmpty(p))
                .collect(Collectors.joining(OsUtils.LINE_SEPARATOR));
    }

    private String validateArgumentOptionValue(String argument, String optionName,
                                               boolean isOptionFound, int optionValue) {
        if (isOptionFound && optionValue <= 0) {
            return String.format("%s value should be greater than zero for --%s argument. ", optionName, argument);
        }
        return "";
    }

    private int getArgumentOptionValue(Pattern pattern, String string) {
        int value = 0;
        Matcher matcher = pattern.matcher(string);
        if (matcher.find()) {
            value = Integer.parseInt(matcher.group(1));
        }
        return value;
    }
}
