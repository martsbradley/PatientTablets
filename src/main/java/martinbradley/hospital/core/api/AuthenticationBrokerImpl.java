package martinbradley.hospital.core.api;

import martinbradley.hospital.core.domain.password.AuthGroup;
import martinbradley.hospital.core.domain.password.Salt;
import martinbradley.hospital.persistence.repository.AuthUserGroupRepo;
import martinbradley.password.PasswordHelper;
import martinbradley.security.JWTFactory;
import martinbradley.security.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.naming.AuthenticationException;
import java.util.Set;

@Model
public class AuthenticationBrokerImpl implements AuthenticationBroker
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationBrokerImpl.class);

    @Inject AuthUserGroupRepo userRepo;

    @Inject JWTFactory jwtFactory;

    @Override
    public JsonWebToken authenticate(String userName, String password) throws AuthenticationException {
 //     // Lookup the database with the user and get the salt.
 //     // SHA 2 ( salt + the password)
 //     // Select the groups from the database that have that user and that hash.
 //     // Use the groups provided to create a JWTString.

        Salt salt = userRepo.getUserSalt(userName);

        PasswordHelper passwordHelper = new PasswordHelper();
        String hashedPassword = passwordHelper.hashPassword(password, salt.getSaltValue());

        JsonWebToken jwt = null;
        try {
            Set<AuthGroup> groups = userRepo.authenticate(userName, hashedPassword);

            logger.debug("authenticate got groups " + groups.size());
            jwt = jwtFactory.createJWT(groups);
        } catch (AuthenticationException e) {
            logger.warn("Failed to authenticate ");
            throw new AuthenticationException(e.getMessage());
        }
        catch (Exception e) {
            logger.warn("Failed to create JWT", e);
            throw new AuthenticationException(e.getMessage());
        }

        return jwt;
    }
}
