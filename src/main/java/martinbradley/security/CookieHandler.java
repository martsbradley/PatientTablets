package martinbradley.security;

import javax.enterprise.inject.New;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import martinbradley.security.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.NewCookie;
import java.util.*;
import java.util.stream.Collectors;

public class CookieHandler {
    private static Logger logger = LoggerFactory.getLogger(CookieHandler.class);
    private static final String AUTH_GROUPS = "authGroups";
    private static final String AUTH_JWT_TOKEN = "jwtToken";
    private static final String AUTH_LOGGED_IN = "userStatus";
    private static Set<String> cookieNames;

    static {
        cookieNames = new HashSet<>();
        cookieNames.add(AUTH_GROUPS);
        cookieNames.add(AUTH_JWT_TOKEN);
        cookieNames.add(AUTH_LOGGED_IN);
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
        String domain = "gorticrum.com";//?
        String comment = "";
        int maxAge = 1800;

        List<NewCookie> cookies = new ArrayList<>();
        cookies.add(new NewCookie(AUTH_GROUPS, groupsStr, path, domain, comment, maxAge, useHttps, httpOnly));
        // DO NOT ALLOW JavaScript access to this cookie.
        // But is this not needed for the UI to figure out what things to present.

        cookies.add(new NewCookie(AUTH_JWT_TOKEN, jSWebToken.toString(), path, domain, comment, maxAge, useHttps, httpOnly));


        cookies.add(new NewCookie(AUTH_LOGGED_IN, "loggedIn", path, domain, comment, maxAge, useHttps, notHttpOnly));

        logger.debug("Added auth0Groups            " + groupsStr);
        logger.debug("Added Cookie jwtToken" );

        // Between the login and the callback the http session
        // stores some data for auth0
        // after this callback is executed that state is no longer needed.
        // removing the session because for a restful frontend there
        // should be no client state stored.
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
