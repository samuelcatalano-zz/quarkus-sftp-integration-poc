package co.uk.olm.group.exception.sftp;

public class SFTPCredentialsException extends Exception {

    public SFTPCredentialsException() {
        super();
    }

    public SFTPCredentialsException(String message) {
        super(message);
    }

    public SFTPCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
