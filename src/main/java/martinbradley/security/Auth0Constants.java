package martinbradley.auth0;

import java.util.Map;

public enum Auth0Constants {
    AUTH0_AUDIENCE,
    AUTH0_CALLBACK_URL,
    AUTH0_CLIENTID,
    AUTH0_CLIENTSECRET,
    AUTH0_DOMAIN   ,
    AUTH0_ISSUER,
    AUTH0_URL,
    AUTH0_LOGIN_FAILURE,
    AUTH0_LOGIN_SUCCESS,
    AUTH0_LOGOUT_SUCCESS,
    AUTH0_NAMESPACE;

    public String getValue() {

        final String name = name();

        Map<String, String> env = System.getenv();


        if (!env.containsKey(name)){
            throw new RuntimeException("Missing environment variable for " + name);
        }

        return env.get(name);
    }
}
