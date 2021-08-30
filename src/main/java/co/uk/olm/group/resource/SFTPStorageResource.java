package co.uk.olm.group.resource;

import co.uk.olm.group.configuration.SFTPStorageClientService;
import co.uk.olm.group.exception.ApiException;
import co.uk.olm.group.exception.sftp.SFTPDownloadException;
import co.uk.olm.group.exception.sftp.SFTPUploadException;
import co.uk.olm.group.model.FileFormData;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Slf4j
@Path("/sftp")
public class SFTPStorageResource {

    @Inject
    SFTPStorageClientService sftpStorageClientService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@MultipartForm final FileFormData formData) throws ApiException {
        try {
            return Response.ok(sftpStorageClientService.upload(formData)).build();
        } catch (final SFTPUploadException e) {
            log.error("Error uploading file to remote host: {}", e.getMessage());
            throw new ApiException("Error uploading file to remote host!", e);
        }
    }

    @GET
    @Path("/download")
    public Response fileDownload(@QueryParam("fileName") final String fileName) throws ApiException {
        try {
            sftpStorageClientService.download(fileName);
            return Response.ok("File successfully downloaded to /tmp/".concat(fileName)).build();
        } catch (final SFTPDownloadException e) {
            log.error("Error downloading file to remote host: {}", e.getMessage());
            throw new ApiException("Error downloading file to remote host!", e);
        }
    }
}