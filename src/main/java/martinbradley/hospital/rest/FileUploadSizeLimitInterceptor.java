package martinbradley.hospital.rest;

import javax.annotation.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Context;
import com.auth0.jwk.JwkException;
import javax.ws.rs.container.ResourceInfo;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

@ContentLengthMethod
@Provider
public class FileUploadSizeLimitInterceptor implements ContainerRequestFilter {

    private static Logger logger = LoggerFactory.getLogger(FileUploadSizeLimitInterceptor.class);
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String contentLength  = requestContext.getHeaderString("content-length");

        logger.warn("FileUploadSizeLimitInterceptor content length = " + contentLength);

        try {
            int length = Integer.parseInt(contentLength);

            if (length > 60000) {
                contentTooLong(requestContext);
            }
        }
        catch (NumberFormatException e) { 
            lengthRequired(requestContext);
        }
    }

    private void contentTooLong(ContainerRequestContext requestContext) {
        logger.warn("Too long sorry");
        requestContext.abortWith(
                Response.status(Response.Status.REQUEST_ENTITY_TOO_LARGE)
                        .build());
    }

    private void lengthRequired(ContainerRequestContext requestContext) {
        logger.warn("lengthRequired");
        requestContext.abortWith(Response.status(
                                 Response.Status.LENGTH_REQUIRED)
                                 .build());
    }
}
