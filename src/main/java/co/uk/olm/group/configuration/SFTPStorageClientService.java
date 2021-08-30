package co.uk.olm.group.configuration;

import co.uk.olm.group.configuration.base.BaseStorageClientService;
import co.uk.olm.group.exception.sftp.SFTPCredentialsException;
import co.uk.olm.group.exception.sftp.SFTPDownloadException;
import co.uk.olm.group.exception.sftp.SFTPUploadException;
import co.uk.olm.group.model.FileFormData;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.io.*;
import java.util.Objects;

@Slf4j
@ApplicationScoped
public class SFTPStorageClientService implements BaseStorageClientService {

    private static final String TARGET_DIR = "/tmp/";
    private static final String CHANNEL_TYPE = "sftp";
    private static final Integer SESSION_TIMEOUT = 10000;
    private static final Integer CHANNEL_TIMEOUT = 50000;

    @ConfigProperty(name = "olmgroup.sftp.id-rsa-filename")
    String idRsaFileName;

    @ConfigProperty(name = "olmgroup.sftp.remote-host")
    String remoteHost;

    @ConfigProperty(name = "olmgroup.sftp.username")
    String username;

    @ConfigProperty(name = "olmgroup.sftp.passphrase")
    String passphrase;

    @ConfigProperty(name = "olmgroup.sftp.default-port")
    Integer remotePort;


    /**
     * Configure SFTP Session.
     * @return session configured
     * @throws SFTPCredentialsException to be launched
     */
    private Session setUpSFTP() throws SFTPCredentialsException {
        try {
            var jsch = new JSch();
            var privateKey = loadSFTPCredentials();
            jsch.addIdentity(privateKey, passphrase);

            var jschSession = jsch.getSession(username, remoteHost, remotePort);
            jschSession.setPassword(passphrase);
            jschSession.setConfig("StrictHostKeyChecking", "no");
            jschSession.connect(SESSION_TIMEOUT);

            return jschSession;
        } catch (final Exception e) {
            throw new SFTPCredentialsException("Error setting sFTP credentials: ", e);
        }
    }

    /**
     * Load sFTP Credentials from id_rsa on resources folder.
     * @return the Google Credentials.
     * @throws SFTPCredentialsException to be launched
     */
    private String loadSFTPCredentials() throws SFTPCredentialsException {
        try {
            return Objects.requireNonNull(getClass().getClassLoader().getResource(idRsaFileName)).getFile();
        } catch (final Exception e) {
            log.error("Error retrieving sFTP Credentials from id_rsa file: {}", e.getMessage());
            throw new SFTPCredentialsException("Error retrieving sFTP Credentials from id_rsa file: ", e);
        }
    }

    /**
     * Upload a file to sFTP remote host channel.
     * @param formData the form which contains the file to be uploaded
     * @return <code>true</code> or <code>false</code>
     * @throws SFTPUploadException to be launched
     */
    @Override
    public String upload(final FileFormData formData) throws SFTPUploadException {
        Session jschSession = null;
        try {
            jschSession = setUpSFTP();
            var sftp = jschSession.openChannel(CHANNEL_TYPE);
            sftp.connect(CHANNEL_TIMEOUT);

            var channelSftp = (ChannelSftp) sftp;
            byte[] bytes = readFully(formData.getData());
            var outputStream = new ByteArrayOutputStream(bytes.length);
            outputStream.writeBytes(bytes);

            channelSftp.put(new ByteArrayInputStream(outputStream.toByteArray()), formData.getFileName());
            channelSftp.exit();

            return "The file has been uploaded to " + remoteHost +"/" + formData.getFileName();
        } catch (final Exception e) {
            log.error("Error uploading file to remote host: {}", e.getMessage());
            throw new SFTPUploadException("Error uploading file to sFTP remote host", e);
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    /**
     * Download a file from Remote Host.
     * @param fileName the name of the file to be downloaded
     * @throws SFTPDownloadException to be launched
     */
    @Override
    public void download(final String fileName) throws SFTPDownloadException {
        Session jschSession = null;
        try {
            jschSession = setUpSFTP();
            var sftp = jschSession.openChannel(CHANNEL_TYPE);
            sftp.connect(CHANNEL_TIMEOUT);

            var channelSftp = (ChannelSftp) sftp;
            var input = channelSftp.get(fileName);
            var output = new ByteArrayOutputStream();

            IOUtils.copy(input, output);
            channelSftp.exit();
            writeFully(fileName, output);

        } catch (final Exception e) {
            log.error("Error downloading file to remote host: {}", e.getMessage());
            throw new SFTPDownloadException("Error downloading file to sFTP remote host", e);
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    /**
     * Read the input stream and convert it to byte array.
     * @param input the input stream
     * @return a byte array converted
     * @throws IOException to be launched
     */
    private byte[] readFully(final InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        var output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    /**
     * Write the file to the target directory.
     * @param fileName the name of the file
     * @param outputStream the output stream
     * @throws IOException to be launched
     */
    private void writeFully(final String fileName, final ByteArrayOutputStream outputStream) throws IOException {
        try (FileOutputStream output = new FileOutputStream(TARGET_DIR + fileName)) {
            output.write(outputStream.toByteArray());
        }
    }
}