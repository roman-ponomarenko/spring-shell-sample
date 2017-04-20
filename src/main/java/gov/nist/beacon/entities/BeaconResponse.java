package gov.nist.beacon.entities;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "record", namespace = "http://beacon.nist.gov/record/0.1/")
public class BeaconResponse {
    @XmlElement(name = "timeStamp")
    private long timeStamp;

    @XmlElement(name = "outputValue")
    private String outputValue;

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getOutputValue() {
        return outputValue;
    }

    @Override
    public String toString() {
        return "BeaconResponse{" +
                "timeStamp=" + timeStamp +
                '}';
    }
}
