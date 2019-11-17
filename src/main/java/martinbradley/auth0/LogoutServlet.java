package martinbradley.auth0;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.servlet.ServletConfig;

import martinbradley.security.Auth0Constants;
//import martinbradley.security.CookieHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    private String domain   = "";
    private String clientId = "";
    private String returnToUrl = "";
    private static Logger logger = LoggerFactory.getLogger(LogoutServlet.class);


    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        logger.debug("QueryString " +request.getQueryString());
        if (request.getSession() != null) {
            request.getSession().invalidate();
            logger.warn("LogoutServlet invalidate called");
        }

        //String returnTo = "https://localhost:3000/logoutsuccess";

        String logoutUrl = "TBD";

        logger.warn("LogoutServlet redirect to '" + logoutUrl + "'");


        // TODO REMEMBER to clear cookies.
   //   CookieHandler cookieHandler = new CookieHandler();
   //   cookieHandler.clearCookies(response);

        response.sendRedirect(logoutUrl);
    }
}
