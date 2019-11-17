package martinbradley.security;


import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Auth0Constants {
    AUTH0_ISSUER;

    private static Logger logger = LoggerFactory.getLogger(Auth0Constants.class);
    public String getValue() {

        final String name = name();

        Map<String, String> env = System.getenv();

        if (!env.containsKey(name)){
            logger.warn("Missing environment variable for " + name);
            throw new RuntimeException("Missing environment variable for " + name);
        }

        return env.get(name);
    }
}
