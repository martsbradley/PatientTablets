package martinbradley.hospital.rest;

import martinbradley.hospital.core.beans.UserPassword;
import martinbradley.hospital.web.api.AuthenticationHandler;
import martinbradley.security.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/authenticate")
public class AuthenticationEndPoint
{
    private static Logger logger = LoggerFactory.getLogger(AuthenticationEndPoint.class);

    @Inject
    AuthenticationHandler authenticationHandler;

    @POST
    @Path("authenticate")
    @Produces("application/json")
    public Response authenticate(UserPassword userDetails)
    {
        logger.warn("authenticate " + userDetails);

        // needs to check the database and create a JWT token if the user is valid.
        try {
            JsonWebToken token = authenticationHandler.authenticate(userDetails);
        }
        catch (Exception e) {
            //denied.
        }

        return Response.accepted()
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
