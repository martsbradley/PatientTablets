package martinbradley.hospital.core.api;

import martinbradley.security.JWTString;

import javax.naming.AuthenticationException;

public interface AuthenticationBroker {
    JWTString authenticate(String userName, String password) throws AuthenticationException;
}
