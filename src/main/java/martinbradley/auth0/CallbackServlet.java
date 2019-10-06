package martinbradley.auth0;

import com.auth0.client.auth.AuthAPI;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Enumeration;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * The Servlet endpoint used as the callback handler in the OAuth 2.0 authorization code grant flow.
 * It will be called with the authorization code after a successful login.
 */
@WebServlet(urlPatterns = {"/auth0callback"})
public class CallbackServlet extends HttpServlet {

    private String redirectOnSuccess = "";
    private String redirectOnFail    = "";
    private String auth0URL          = "";
    private String auth0Issuer       = "";
    private String auth0Namespace    = "";
    private Auth0RSASolution auth0RSA;

    @Inject
    private UserProfileQuery userProfileQuery;


    private AuthAPI authAPI;
    private static Logger logger = LoggerFactory.getLogger(CallbackServlet.class);
    private Auth0Verifier verifier;

    /**
     * Initialize this servlet with required configuration.
     * <p>
     * Parameters needed on the Local Servlet scope:
     * <ul>
     * <li>'com.auth0.redirect_on_success': where to redirect after a successful authentication.</li>
     * <li>'com.auth0.redirect_on_error': where to redirect after a failed authentication.</li>
     * </ul>
     * Parameters needed on the Local/Global Servlet scope:
     * <ul>
     * <li>'com.auth0.domain': the Auth0 domain.</li>
     * <li>'com.auth0.client_id': the Auth0 Client id.</li>
     * <li>'com.auth0.client_secret': the Auth0 Client secret.</li>
     * </ul>
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        final ServletContext context  = config.getServletContext(); 
        redirectOnSuccess =Auth0Constants.AUTH0_LOGIN_SUCCESS.getValue();
        redirectOnFail    =Auth0Constants.AUTH0_LOGIN_FAILURE.getValue();
        auth0Namespace    =Auth0Constants.AUTH0_NAMESPACE.getValue();
        auth0Issuer       =Auth0Constants.AUTH0_ISSUER.getValue();
        auth0URL          =Auth0Constants.AUTH0_URL.getValue();
        
        try {
            authAPI = new AuthenticationControllerProvider(config).getAuthAPI();

            Auth0KeyProvider provider = new Auth0KeyProvider(auth0URL);
            
            verifier = new Auth0Verifier(auth0Issuer, 
                                         provider.getPublicKey(null)); 
        }
        catch (com.auth0.jwk.JwkException e) {
            logger.warn("JwkException in init ", e);
            throw new ServletException("Couldn't create the AuthenticationController instance. Check the configuration.", e);
        }
        catch (UnsupportedEncodingException e) {
            throw new ServletException("Couldn't create the AuthenticationController instance. Check the configuration.", e);
        }
    }

    /**
     * Process a call to the redirect_uri with a POST HTTP method. This occurs if the authorize_url included the 'response_mode=form_post' value.
     * This is disabled by default. On the Local Servlet scope you can specify the 'com.auth0.allow_post' parameter to enable this behaviour.
     *
     * @param req the received request with the tokens (implicit grant) or the authorization code (code grant) in the parameters.
     * @param res the response to send back to the server.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        logger.warn("doPost Called");
        handle(req, res);
    }
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        logger.warn("doGet Called");
        handle(req, res);
    }

    private void handle(HttpServletRequest req, HttpServletResponse res) throws IOException {
        logger.debug("QueryString " +req.getQueryString());

        try {
            handleCookies(req, res);

            HttpSession session = req.getSession(false);

            if (session != null) {
                session.invalidate();
                logger.debug("Deleted session");
                // session only needed for the auth0 callback code.
                // session will not record client state since 
                // the jwt is passed in each request.
            }
            logger.debug("Success Redirecting to " + redirectOnSuccess);
            res.sendRedirect(redirectOnSuccess);

        } catch (Exception e) {
            /* Had been catching IdentityVerificationException 
             * But that exception cannot be created outside auth0 package.
             * So catching Exception to make it testable */
            logger.warn("Error in CallbackServlet '" + e.getMessage());
            logger.warn("Success Redirecting to " + redirectOnFail);
            res.sendRedirect(redirectOnFail);
        }
    }

    private void handleCookies(HttpServletRequest req, HttpServletResponse res) 
        throws Exception {

        String jSWebToken = req.getParameter("access_token");
        // An exception is thrown if the token not valid.
        Set<String> groups = verifier.readGroups(jSWebToken, auth0Namespace);
        for (String group: groups) {
            logger.warn("Got group: " + group);
        }

        String groupsStr = groups.stream().collect(Collectors.joining(","));
        
        String expiresInStr = req.getParameter("expires_in");

        CookieHandler cookieHandler = new CookieHandler();
        logger.info("JWT token is " + jSWebToken);

        cookieHandler.handleSuccessfulLogin(res, jSWebToken, groupsStr, expiresInStr);

        userProfileQuery.handleGettingProfile(authAPI, jSWebToken, groupsStr);
    }
}
