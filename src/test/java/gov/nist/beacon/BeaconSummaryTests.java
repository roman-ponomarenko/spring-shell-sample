package gov.nist.beacon;

import gov.nist.beacon.utils.StringUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BeaconSummaryTests extends BaseTests {
    @Test
    public void shortSumTest() {
        String beaconSummary = StringUtils.getBeaconSummary("B423AF");
        assertThat(beaconSummary, is("2,1\n3,1\n4,1\nA,1\nB,1\nF,1"));
    }

    @Test
    public void longSumTest() {
        String beaconSummary = StringUtils.getBeaconSummary("B423AF7352BA");
        assertThat(beaconSummary, is("2,2\n3,2\n4,1\n5,1\n7,1\nA,2\nB,2\nF,1"));
    }
}
