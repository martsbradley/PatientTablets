package martinbradley.hospital.web.api;

import martinbradley.hospital.core.api.AuthenticationBroker;
import martinbradley.hospital.core.beans.UserPassword;
import martinbradley.security.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.AuthenticationException;

@ApplicationScoped
@Named
public class AuthenticationHandler
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationHandler.class);

    @Inject AuthenticationBroker authBroker;

    public JsonWebToken authenticate(UserPassword userPassword) throws AuthenticationException
    {
        return authBroker.authenticate(userPassword.getUsername(),
                                       userPassword.getPassword());
    }
}
