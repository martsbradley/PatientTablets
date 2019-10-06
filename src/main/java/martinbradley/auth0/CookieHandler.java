package martinbradley.auth0;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class CookieHandler {
    private static Logger logger = LoggerFactory.getLogger(CookieHandler.class);
    private static final String AUTH0_GROUPS = "auth0Groups";
    private static final String AUTH0_JWT_TOKEN = "jwtToken";
    private static final String AUTH0_EXPIRES_IN = "auth0ExpiresIn";
    private static final String AUTH0_LOGGED_IN = "userStatus";
    private static Set<String> cookieNames;

    static {
        cookieNames = new HashSet<>();
        cookieNames.add(AUTH0_LOGGED_IN);
        cookieNames.add(AUTH0_GROUPS);
        cookieNames.add(AUTH0_JWT_TOKEN);
        cookieNames.add(AUTH0_EXPIRES_IN);
        cookieNames.add(AUTH0_LOGGED_IN);
    }
                                         
    public void handleSuccessfulLogin(HttpServletResponse res, 
                                      String jSWebToken,
                                      String groupsStr,
                                      String expiresInStr) throws Exception {
        final boolean useHttps = true;

        Cookie groupsCookie = new Cookie(AUTH0_GROUPS, groupsStr);
        groupsCookie.setSecure(useHttps);
        groupsCookie.setPath("/");
        groupsCookie.setHttpOnly(true);// DO NOT ALLOW JavaScript access to this cookie.

        Cookie jwtCookie = new Cookie(AUTH0_JWT_TOKEN, jSWebToken);
        jwtCookie.setSecure(useHttps);
        jwtCookie.setHttpOnly(true);// DO NOT ALLOW JavaScript access to this cookie.
        jwtCookie.setPath("/");

        Cookie jwtExpiresIn = new Cookie(AUTH0_EXPIRES_IN, expiresInStr);
        jwtExpiresIn.setSecure(useHttps);
        jwtExpiresIn.setPath("/");

        Cookie userStatus = new Cookie(AUTH0_LOGGED_IN, "loggedIn");
        userStatus.setSecure(useHttps);
        userStatus.setPath("/");

        res.addCookie(groupsCookie);
        res.addCookie(jwtCookie);
        res.addCookie(jwtExpiresIn);
        res.addCookie(userStatus);

        logger.debug("Added auth0Groups            " + groupsStr);
        logger.debug("Added Cookie auth0ExpiresIn" + expiresInStr);
        logger.debug("Added Cookie jwtToken" );

        // Between the login and the callback the http session
        // stores some data for auth0
        // after this callback is executed that state is no longer needed.
        // removing the session because for a restful frontend there
        // should be no client state stored.
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
