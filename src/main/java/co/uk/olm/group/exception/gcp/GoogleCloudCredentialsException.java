package co.uk.olm.group.exception.gcp;

public class GoogleCloudCredentialsException extends Exception {

    public GoogleCloudCredentialsException() {
        super();
    }

    public GoogleCloudCredentialsException(String message) {
        super(message);
    }

    public GoogleCloudCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
