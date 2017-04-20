package gov.nist.beacon.entities;

public class TimeOptions {
    private int month;
    private int days;
    private int hours;
    private int minutes;

    private TimeOptions(int month, int days, int hours, int minutes) {
        this.month = month;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getMonth() {
        return month;
    }

    public int getDays() {
        return days;
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TimeOptions)) {
            return false;
        }

        TimeOptions that = (TimeOptions) o;

        return month == that.month
                && days == that.days
                && hours == that.hours
                && minutes == that.minutes;
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + days;
        result = 31 * result + hours;
        result = 31 * result + minutes;
        return result;
    }

    @Override
    public String toString() {
        return "TimeOptions{" +
                "month=" + month +
                ", days=" + days +
                ", hours=" + hours +
                ", minutes=" + minutes +
                '}';
    }

    public static TimeOptions of(int month, int days, int hours, int minutes) {
        return new TimeOptions(month, days, hours, minutes);
    }
}
