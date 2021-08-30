package co.uk.olm.group.exception.gcp;

public class GoogleCloudDownloadException extends Exception {

    public GoogleCloudDownloadException() {
        super();
    }

    public GoogleCloudDownloadException(String message) {
        super(message);
    }

    public GoogleCloudDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
