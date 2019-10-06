package martinbradley.auth0;

import org.junit.jupiter.api.Test;
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
//import com.auth0.AuthorizeUrl;
import com.auth0.client.auth.AuthAPI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServletTest {
    private static Logger logger = LoggerFactory.getLogger(LoginServletTest.class);

    @Mocked ServletConfig config;
    @Mocked ServletContext context;
    @Mocked AuthAPI controller;
    //@Mocked AuthorizeUrl authUrl;
    @Mocked HttpServletRequest request;
    @Mocked HttpServletResponse response;

    LoginServlet servlet = new LoginServlet();

    private void setupWebxml(final String domain, 
                             final String callbackURL, 
                             final String audience) {
        new Expectations(){{
            config.getServletContext(); result = context;
            context.getInitParameter("com.auth0.domain");   result = domain;
            context.getInitParameter("AUTH0_CALLBACK_URL"); result = callbackURL;
            context.getInitParameter("AUTH0_AUDIENCE");     result = audience;
        }};
    }

  ///** Test - not a great test that checks the values are read from 
  // * the web.xml and that they are passed to the auth0 classes
  // */
  //@Test
  //public void getMethod_PassesValues() throws Exception {

  //    final String callback = "callbackB";
  //    final String audience = "audienceC";

  //    setupWebxml("domainA",callback, audience);
  //                                                     
  //    new Expectations(){{
  //        controller.buildAuthorizeUrl((HttpServletRequest)any, callback); result = authUrl;
  //        authUrl.withAudience(audience);
  //    }};

  //    servlet.init(config);
  //    servlet.doGet(request, response);
  //}
}
