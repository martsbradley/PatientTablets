package martinbradley.hospital.rest;

import javax.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.validation.constraints.*;
 
public class ImageUploadedBean {
    private static Logger logger = LoggerFactory.getLogger(ImageUploadedBean.class);
 
    @Size(min=5, max=250)
    private String description = "";

    @NotNull
    private byte[] data;
 
    public byte[] getData() {
        return data;
    }
 
    @FormParam("uploadedFile")
    @PartType("application/octet-stream")
    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    @FormParam("description")
    @PartType("application/text")
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ImageUploaded [");
        if (data != null) {
            sb.append("data is ");
            sb.append(data.length);
            sb.append(" bytes long");
        }
        else {
            sb.append("data is null ");
        }
        sb.append(" description: ");
        sb.append(description);
        sb.append("]");
        return sb.toString();
    }
}
