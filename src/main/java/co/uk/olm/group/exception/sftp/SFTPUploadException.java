package co.uk.olm.group.exception.sftp;

public class SFTPUploadException extends Exception {

    public SFTPUploadException() {
        super();
    }

    public SFTPUploadException(String message) {
        super(message);
    }

    public SFTPUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
