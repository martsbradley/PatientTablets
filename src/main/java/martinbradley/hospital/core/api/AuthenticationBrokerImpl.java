package martinbradley.hospital.core.api;

import martinbradley.hospital.core.domain.password.AuthGroup;
import martinbradley.hospital.core.domain.password.Salt;
import martinbradley.hospital.persistence.repository.AuthUserGroupRepo;
import martinbradley.password.PasswordHelper;
import martinbradley.security.AuthenticationConstants;
import martinbradley.security.JsonWebToken;
import martinbradley.security.KeyStoreLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Model
public class AuthenticationBrokerImpl implements AuthenticationBroker
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationBrokerImpl.class);

    @Inject AuthUserGroupRepo userRepo;

    private KeyStoreLoader keyLoader;
    public AuthenticationBrokerImpl() {

        keyLoader = new KeyStoreLoader(AuthenticationConstants.AUTH_ISSUER.getValue(),
                                       AuthenticationConstants.AUTH_DOMAIN.getValue());
    }

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

            jwt = buildJwt(userName, groups);
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

    private JsonWebToken buildJwt(String userName, Set<AuthGroup> groups) throws Exception {
        JsonWebToken.Builder builder = new JsonWebToken.Builder();

        String issuer = AuthenticationConstants.AUTH_ISSUER.getValue();
        builder.setIssuer(issuer);

        String[] groupsArr = new String[groups.size()];
        int idx = 0;
        for (AuthGroup group: groups) {
            groupsArr[idx++] = group.getName();
        }

        String namespace = AuthenticationConstants.AUTH_DOMAIN.getValue();
        builder.setGroups(namespace, groupsArr);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(10);

        long issuedAt = toSinceEpoch(now);
        long expires  = toSinceEpoch(expiryTime);

        builder.setIat(issuedAt);
        builder.setExp(expires);
        builder.setSubject(userName);

        JsonWebToken jwt = builder.build();
        jwt.sign(keyLoader.getKeyPair());
        return jwt;
    }

    private long toSinceEpoch(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}
