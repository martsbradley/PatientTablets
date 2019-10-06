package martinbradley.auth0;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private String domain   = "";
    private String clientId = "";
    private String returnToUrl = "";
    private static Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        
        domain      = Auth0Constants.AUTH0_DOMAIN.getValue();
        clientId    = Auth0Constants.AUTH0_CLIENTID.getValue();
        returnToUrl = Auth0Constants.AUTH0_LOGOUT_SUCCESS.getValue();
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        logger.debug("QueryString " +request.getQueryString());
        if (request.getSession() != null) {
            request.getSession().invalidate();
            logger.warn("LogoutServlet invalidate called");
        }

        //String returnTo = "https://localhost:3000/logoutsuccess";

        String logoutUrl = String.format("https://%s/v2/logout?client_id=%s&returnTo=%s", domain, clientId, returnToUrl);

        logger.warn("LogoutServlet redirect to '" + logoutUrl + "'");

        CookieHandler cookieHandler = new CookieHandler();
        cookieHandler.clearCookies(response);

        response.sendRedirect(logoutUrl);
    }
}
