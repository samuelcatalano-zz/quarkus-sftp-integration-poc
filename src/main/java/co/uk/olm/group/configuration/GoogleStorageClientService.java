package co.uk.olm.group.configuration;

import co.uk.olm.group.configuration.base.BaseStorageClientService;
import co.uk.olm.group.exception.gcp.GoogleCloudCredentialsException;
import co.uk.olm.group.exception.gcp.GoogleCloudDownloadException;
import co.uk.olm.group.exception.gcp.GoogleCloudUploadException;
import co.uk.olm.group.model.FileFormData;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
@ApplicationScoped
public class GoogleStorageClientService implements BaseStorageClientService {

    private static final String TARGET_DIR = "/tmp/";

    @ConfigProperty(name = "google.bucket.name")
    String bucketName;

    /**
     * Upload a file to Google Cloud Storage bucket.
     * @param file the file to be uploaded
     * @return the file media link
     * @throws GoogleCloudUploadException to be launched
     */
    @Override
    public String upload(final FileFormData file) throws GoogleCloudUploadException {
        try {
            var credentials = loadGoogleCloudCredentials();
            var storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            var blobInfo = storage.create(BlobInfo.newBuilder(bucketName, file.getFileName()).build());

            storage.create(blobInfo, file.data.readAllBytes());
            return blobInfo.getMediaLink();
        } catch (final Exception e) {
            log.error("Error uploading file to GCP: {}", e.getMessage());
            throw new GoogleCloudUploadException("Error uploading file to GCP", e);
        }
    }

    /**
     * Download a file from Google Cloud Storage bucket.
     * @param fileName the name of the file to be downloaded
     * @throws GoogleCloudDownloadException to be launched
     */
    @Override
    public void download(final String fileName) throws GoogleCloudDownloadException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(TARGET_DIR + fileName)) {
            var credentials = loadGoogleCloudCredentials();
            var storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            var blobId = BlobId.of(bucketName, fileName);
            var blob = storage.get(blobId);
            var readChannel = blob.reader();

            fileOutputStream.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
        } catch (final Exception e) {
            log.error("Error downloading file to GCP: {}", e.getMessage());
            throw new GoogleCloudDownloadException("Error downloading file to GCP", e);
        }
    }

    /**
     * Load GCP Credentials from json file on resources folder.
     * @return the Google Credentials.
     * @throws GoogleCloudCredentialsException to be launched
     */
    private GoogleCredentials loadGoogleCloudCredentials() throws GoogleCloudCredentialsException {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final InputStream resource = classLoader.getResourceAsStream("google_credentials.json");

            return GoogleCredentials.fromStream(Objects.requireNonNull(resource));
        } catch (final Exception e) {
            log.error("Error retrieving Google Cloud Credentials: {}", e.getMessage());
            throw new GoogleCloudCredentialsException("Error retrieving Google Cloud Credentials", e);
        }
    }
}