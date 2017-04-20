package gov.nist.beacon;

import gov.nist.beacon.commands.BeaconServiceCommands;
import gov.nist.beacon.entities.TimeOptions;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.CommandResult;
import org.springframework.shell.core.JLineShellComponent;
import ru.stqa.trier.LimitExceededException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static gov.nist.beacon.commands.BeaconServiceCommands.SUMMARIZE_BEACON;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.Duration.TWO_MINUTES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BeaconServiceCommandsTests extends BaseTests {
    @Autowired
    private BeaconServiceCommands beaconServiceCommands;

    @Autowired
    private Bootstrap bootstrap;

    @Autowired
    private BeaconSummarizer summarizer;

    public void waitTillBeggingOfNewMinute() {
        int minute = LocalDateTime.now().with(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)).getMinute();
        with().pollInterval(1, SECONDS).atMost(TWO_MINUTES).await().until(() ->
                LocalDateTime.now().with(LocalTime.now().truncatedTo(ChronoUnit.MINUTES)).getMinute() == minute + 1);
    }

    @Test
    public void noOptionalArgumentsTest() {
        waitTillBeggingOfNewMinute();
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        CommandResult cr = shell.executeCommand(SUMMARIZE_BEACON);

        assertThat(cr.isSuccess(), is(true));
        assertThat(cr.getResult(), is(summarizer.distinguish()));
    }

    @Test
    public void withOptionalArgumentsTest() {
        waitTillBeggingOfNewMinute();
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        String from = "5 minutes ago";
        String to = "1 minutes ago";
        TimeOptions fromOps = beaconServiceCommands.getArgumentOptions(from);
        TimeOptions toOps = beaconServiceCommands.getArgumentOptions(to);
        CommandResult cr = shell.executeCommand(SUMMARIZE_BEACON + String.format(" --from \"%s\" --to \"%s\"", from, to));

        assertThat(cr.isSuccess(), is(true));
        assertThat(cr.getResult(), is(summarizer.distinguish(fromOps, toOps)));
    }

    @Test
    public void zeroTimeOptionsTest() {
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        String from = "0 months 0 day 0 hour 0 minutes ago";
        String to = "0 months 0 day 0 hour 0 minutes ago";
        CommandResult cr = shell.executeCommand(SUMMARIZE_BEACON + String.format(" --from \"%s\" --to \"%s\"", from, to));

        assertThat(cr.isSuccess(), is(true));
        assertThat(cr.getResult(), equalTo("Month(s) value should be greater than zero for --from argument. " +
                "\nDay(s) value should be greater than zero for --from argument. " +
                "\nHour(s) value should be greater than zero for --from argument. " +
                "\nMinute(s) value should be greater than zero for --from argument. " +
                "\nMonth(s) value should be greater than zero for --from argument. " +
                "\nDay(s) value should be greater than zero for --from argument. " +
                "\nHour(s) value should be greater than zero for --from argument. " +
                "\nMinute(s) value should be greater than zero for --from argument. "));
    }

    @Test
    public void oneOptionalArgumentTest() {
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        String options = "2 day 5 hour 6 minutes ago";
        CommandResult crFrom = shell.executeCommand(SUMMARIZE_BEACON + String.format(" --from \"%s\"", options));
        CommandResult toFrom = shell.executeCommand(SUMMARIZE_BEACON + String.format(" --to \"%s\"", options));

        assertThat(crFrom.isSuccess(), is(true));
        assertThat(crFrom.getResult(), equalTo("Only one optional argument is specified."));

        assertThat(toFrom.isSuccess(), is(true));
        assertThat(toFrom.getResult(), equalTo("Only one optional argument is specified."));
    }

    @Test
    public void invalidTimeOptionsTest() {
        JLineShellComponent shell = bootstrap.getJLineShellComponent();
        String from = "3months 4day 5hour 12minutes ago";
        String to = "1months 6day 7hour 26minutes ago";
        CommandResult cr = shell.executeCommand(SUMMARIZE_BEACON + String.format(" --from \"%s\" --to \"%s\"", from, to));

        assertThat(cr.isSuccess(), is(true));
        assertThat(cr.getResult(), equalTo("None of options is valid for argument --from" +
                "\nNone of options is valid for argument --from"));
    }
}
