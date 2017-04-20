package gov.nist.beacon;

import gov.nist.beacon.commands.BeaconServiceCommands;
import gov.nist.beacon.entities.TimeOptions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static gov.nist.beacon.matchers.Matchers.haveSameTimeOptions;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

public class BeaconServiceInternalsTests extends BaseTests {
    @Autowired
    private BeaconServiceCommands beaconServiceCommands;

    @Test
    public void validMonthsDaysHoursMinutesTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("3 months 2 day 6 hour 5 minutes ago");
        TimeOptions expOps = TimeOptions.of(3, 2, 6, 5);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validMonthsDaysHoursTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("2 months 4 day 8 hour ago");
        TimeOptions expOps = TimeOptions.of(2, 4, 8, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validMonthsDaysTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("9 months 2 day ago");
        TimeOptions expOps = TimeOptions.of(9, 2, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validMonthsTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("4 months ago");
        TimeOptions expOps = TimeOptions.of(4, 0, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validDaysTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("1 days ago");
        TimeOptions expOps = TimeOptions.of(0, 1, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validHoursTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("9 hours ago");
        TimeOptions expOps = TimeOptions.of(0, 0, 9, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validMinutesTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("34 minutes ago");
        TimeOptions expOps = TimeOptions.of(0, 0, 0, 34);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void invalidMonthsTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("4months ago");
        TimeOptions expOps = TimeOptions.of(0, 0, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void invalidDaysTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("1days ago");
        TimeOptions expOps = TimeOptions.of(0, 0, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void invalidHoursTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("9hours ago");
        TimeOptions expOps = TimeOptions.of(0, 0, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void invalidMinutesTest() {
        TimeOptions actOps = beaconServiceCommands.getArgumentOptions("34minutes ago");
        TimeOptions expOps = TimeOptions.of(0, 0, 0, 0);
        assertThat(actOps, haveSameTimeOptions(expOps));
    }

    @Test
    public void validInputForValidateTest() {
        String actualResult = beaconServiceCommands.validate("from", "3 months 2 day 6 hour 5 minutes ago");
        assertThat(actualResult, isEmptyString());
    }

    @Test
    public void invalidInputForValidateTest() {
        String actualResult = beaconServiceCommands.validate("from", "3months 2day 6hour 5minutes ago");
        assertThat(actualResult, is("None of options is valid for argument --from"));
    }

    @Test
    public void zeroOptionsToValidateTest() {
        String actualResult = beaconServiceCommands.validate("from", "0 months 0 day 0 hour 0 minutes ago");
        assertThat(actualResult, is("Month(s) value should be greater than zero for --from argument. " +
                "\nDay(s) value should be greater than zero for --from argument. " +
                "\nHour(s) value should be greater than zero for --from argument. " +
                "\nMinute(s) value should be greater than zero for --from argument. "));
    }
}
