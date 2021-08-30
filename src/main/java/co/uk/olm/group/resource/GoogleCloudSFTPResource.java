package co.uk.olm.group.resource;

import co.uk.olm.group.configuration.GoogleStorageClientService;
import co.uk.olm.group.exception.ApiException;
import co.uk.olm.group.exception.gcp.GoogleCloudDownloadException;
import co.uk.olm.group.exception.gcp.GoogleCloudUploadException;
import co.uk.olm.group.model.FileFormData;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/gcp")
public class GoogleCloudSFTPResource {

    @Inject
    GoogleStorageClientService googleStorageClientService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFile(@MultipartForm final FileFormData formData) throws ApiException {
        try {
            return Response.ok(googleStorageClientService.upload(formData)).build();
        } catch (final GoogleCloudUploadException e) {
            log.error("Error uploading file to GCP: {}", e.getMessage());
            throw new ApiException("Error uploading file to GCP!", e);
        }
    }

    @GET
    @Path("/download")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fileDownload(@QueryParam("fileName") final String fileName) throws ApiException {
        try {
            googleStorageClientService.download(fileName);
            return Response.ok("File successfully downloaded to /tmp/".concat(fileName)).build();
        } catch (final GoogleCloudDownloadException e) {
            log.error("Error downloading file to GCP: {}", e.getMessage());
            throw new ApiException("Error downloading file to GCP!", e);
        }
    }
}