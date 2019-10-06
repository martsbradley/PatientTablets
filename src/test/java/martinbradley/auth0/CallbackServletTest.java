package martinbradley.auth0;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import mockit.*;
import com.auth0.client.auth.AuthAPI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

public class CallbackServletTest {
    private static Logger logger = LoggerFactory.getLogger(CallbackServletTest.class);

    @Mocked ServletConfig config;
    @Mocked ServletContext context;
    @Mocked AuthAPI authController;
    @Mocked Auth0Verifier authVerifier;
    @Mocked HttpServletRequest request;
    @Mocked HttpServletResponse response;
    //@Mocked Tokens tokens;

    final String successURL = "http://itworked";
    final String failureURL ="http://hardluck";

    @BeforeEach
    public void beforeEach() {
        //setupWebxml();
    }

    CallbackServlet servlet = new CallbackServlet();

/// private void setupWebxml() {
///     new Expectations(){{
///         config.getServletContext(); result = context;
///         context.getInitParameter("AUTH0_LOGIN_SUCCESS"); result = successURL;
///         context.getInitParameter("AUTH0_LOGIN_FAILURE"); result = failureURL;

///         context.getInitParameter(CallbackServlet.AUTH0_URL);    result = "https://localhosthttpstennant.eu.auth0.com";
///         context.getInitParameter(CallbackServlet.AUTH0_ISSUER); result = "https://localhosthttpstennant.eu.auth0.com/";
///     }};
/// }

  //@Test
  //public void handlePostSuccess() throws Exception {

  //    new Expectations(){{
  //        authController.handle((HttpServletRequest)any); result = tokens;
  //        tokens.getAccessToken(); result = "letmein";
  //        response.sendRedirect(successURL);

  //        HashSet<String> groups = new HashSet<>();
  //        groups.add("admin");
  //        groups.add("partypeople");
  //        authVerifier.readGroups(anyString, anyString); result = groups;
  //    }};

  //    servlet.init(config);
  //    servlet.doPost(request, response);
  //}
  //@Test
  //public void handlePostFailure() throws Exception {
  //    new Expectations(){{
  //        authController.handle((HttpServletRequest)any); 
  //        result = new Exception("Failed");

  //        response.sendRedirect(failureURL);
  //    }};

  //    servlet.init(config);
  //    servlet.doPost(request, response);
  //}
}
