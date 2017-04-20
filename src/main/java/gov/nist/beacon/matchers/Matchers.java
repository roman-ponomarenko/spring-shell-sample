package gov.nist.beacon.matchers;

import gov.nist.beacon.entities.TimeOptions;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {
    private Matchers() {
    }

    public static Matcher<TimeOptions> haveSameTimeOptions(TimeOptions expectedOptions) {
        return new TypeSafeMatcher<TimeOptions>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Expected options should be ").appendValue(expectedOptions);
            }

            @Override
            protected void describeMismatchSafely(TimeOptions item, Description mismatchDescription) {
                mismatchDescription.appendText(" was ").appendValue(item);
            }

            @Override
            protected boolean matchesSafely(TimeOptions item) {
                return expectedOptions.equals(item);
            }
        };
    }
}
