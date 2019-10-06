package martinbradley.auth0;


import com.auth0.client.auth.AuthAPI;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth0.client.mgmt.UsersEntity;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private AuthAPI authAPI;
    private String domain = "", callbackURL = "", audience = ""; 
    private static Logger logger = LoggerFactory.getLogger(AuthenticationControllerProvider.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        logger.warn("LoginServlet init called");

        domain      = Auth0Constants.AUTH0_DOMAIN.getValue();
        callbackURL = Auth0Constants.AUTH0_CALLBACK_URL.getValue();
        audience    = Auth0Constants.AUTH0_AUDIENCE.getValue();

        try {
            authAPI = new AuthenticationControllerProvider(config)
                                                            .getAuthAPI();
        } catch (UnsupportedEncodingException e) {
            logger.warn("Cannot init LoginServlet '" + e.getMessage() + "'");
            throw new ServletException("Could not create the AuthenticationController instance.", e);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {

        logger.warn("LoginServlet audience '" + audience + "'");
        logger.warn("callbackURL '" + callbackURL + "'");

        String authorizeUrl = authAPI.authorizeUrl(callbackURL)
                                     .withAudience(audience)
                                     .withResponseType("token")// Needed to get the token in the callback.
                                     .withParameter("response_mode","form_post")// the call back is via POST rather than GET
                                     .withScope("profile token openid")
                                     .build();

        logger.warn("LoginServlet redirecting to " + authorizeUrl);

        res.sendRedirect(authorizeUrl);
    }
}
