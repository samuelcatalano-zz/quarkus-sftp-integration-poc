package co.uk.olm.group.exception.gcp;

public class GoogleCloudUploadException extends Exception {

    public GoogleCloudUploadException() {
        super();
    }

    public GoogleCloudUploadException(String message) {
        super(message);
    }

    public GoogleCloudUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
