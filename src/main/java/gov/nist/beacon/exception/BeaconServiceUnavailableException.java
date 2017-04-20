package gov.nist.beacon.exception;

public class BeaconServiceUnavailableException extends RuntimeException {
    public BeaconServiceUnavailableException(String message) {
        super(message);
    }
}
