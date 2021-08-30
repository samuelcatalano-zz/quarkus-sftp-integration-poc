package co.uk.olm.group.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileFormData {

    @FormParam("file")
    @PartType(MediaType.MULTIPART_FORM_DATA)
    public InputStream data;

    @FormParam("filename")
    @PartType(MediaType.TEXT_PLAIN)
    public String fileName;
}
