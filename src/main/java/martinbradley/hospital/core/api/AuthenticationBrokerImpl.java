package martinbradley.hospital.core.api;

import martinbradley.hospital.core.domain.password.AuthGroup;
import martinbradley.hospital.core.domain.password.Salt;
import martinbradley.hospital.persistence.repository.AuthUserGroupRepo;
import martinbradley.password.PasswordHelper;
import martinbradley.security.JWTString;

import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.naming.AuthenticationException;
import java.util.Set;

@Model
public class AuthenticationBrokerImpl implements AuthenticationBroker
{
    @Inject
    AuthUserGroupRepo userRepo;

    @Override
    public JWTString authenticate(String userName, String password) throws AuthenticationException {
        // Lookup the database with the user and get the salt.
        // SHA 2 ( salt + the password)
        // Select the groups from the database that have that user and that hash.
        // Use the groups provided to create a JWTString.

        Salt salt = userRepo.getUserSalt(userName);

        PasswordHelper passwordHelper = new PasswordHelper();
        String hashedPassword = passwordHelper.hashPassword(password, salt.getSaltValue());

        Set<AuthGroup> groups = userRepo.authenticate(userName, hashedPassword);

        passwordHelper.hashPassword("marty", "bradley");

        return null;
    }
}
