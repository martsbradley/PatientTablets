package martinbradley.security;

import javax.servlet.http.Cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;
import java.util.*;

public class CookieHandler {
    private static Logger logger = LoggerFactory.getLogger(CookieHandler.class);
    private static final String AUTH_GROUPS = "authGroups";
    private static final String AUTH_JWT_TOKEN = "jwtToken";
    private static final String AUTH_LOGGED_IN = "userStatus";
    private static final String domain;
    private static Set<String> cookieNames;

    static {
        cookieNames = new HashSet<>();
        cookieNames.add(AUTH_GROUPS);
        cookieNames.add(AUTH_JWT_TOKEN);
        cookieNames.add(AUTH_LOGGED_IN);
        domain = AuthenticationConstants.AUTH_DOMAIN.getValue();
    }
                                         
    public List<NewCookie> handleSuccessfulLogin(JsonWebToken jSWebToken) throws Exception {
        final boolean useHttps = true;
        final boolean httpOnly = true;
        final boolean notHttpOnly = false;


        final String [] groups = jSWebToken.getGroups();

        StringJoiner j = new StringJoiner(",");
        for (String group: groups) {
            j.add(group);
        }
        String groupsStr = j.toString();
        logger.info("Groups " + groupsStr);

        String path = "/";
        String comment = "";
        int maxAge = 1800;

        List<NewCookie> cookies = new ArrayList<>();
        cookies.add(new NewCookie(AUTH_GROUPS, groupsStr, path, domain, comment, maxAge, useHttps, httpOnly));
        // DO NOT ALLOW JavaScript access to this cookie.
        // But is this not needed for the UI to figure out what things to present.

        cookies.add(new NewCookie(AUTH_JWT_TOKEN, jSWebToken.toString(), path, domain, comment, maxAge, useHttps, httpOnly));


        cookies.add(new NewCookie(AUTH_LOGGED_IN, "loggedIn", path, domain, comment, maxAge, useHttps, notHttpOnly));

        logger.debug("Added groups            " + groupsStr);
        logger.debug("Added Cookie jwtToken" );

        return cookies;
    }
    public void clearCookies(HttpServletResponse response) {
        for (String cookieName : cookieNames){
            Cookie cookie = new Cookie(cookieName, null); // Not necessary, but saves bandwidth.
            cookie.setPath("/");

            cookie.setHttpOnly(true);

            cookie.setMaxAge(0); // Don't set to -1 or it will become a session cookie!
            response.addCookie(cookie);
            logger.debug("Clearing " + cookieName);
        }
    }
}
