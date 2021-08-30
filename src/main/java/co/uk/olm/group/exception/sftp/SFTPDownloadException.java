package co.uk.olm.group.exception.sftp;

public class SFTPDownloadException extends Exception {

    public SFTPDownloadException() {
        super();
    }

    public SFTPDownloadException(String message) {
        super(message);
    }

    public SFTPDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
