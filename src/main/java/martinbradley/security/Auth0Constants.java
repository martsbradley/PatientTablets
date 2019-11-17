package martinbradley.security;

import java.util.Map;

public enum Auth0Constants {
    AUTH0_ISSUER;

    public String getValue() {

        final String name = name();

        Map<String, String> env = System.getenv();

        if (!env.containsKey(name)){
            throw new RuntimeException("Missing environment variable for " + name);
        }

        return env.get(name);
    }
}
