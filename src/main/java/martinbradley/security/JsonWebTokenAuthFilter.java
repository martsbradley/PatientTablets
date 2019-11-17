package martinbradley.security;

import com.auth0.jwk.JwkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Map;

@SecuredRestfulMethod
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JsonWebTokenAuthFilter implements ContainerRequestFilter {

  private static Logger logger = LoggerFactory.getLogger(JsonWebTokenAuthFilter.class);
    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    private final String AUTH0_ISSUER = "AUTH0_ISSUER";
    @Context ResourceInfo resourceInfo;
    private String auth0Issuer;

//  private JsonWebTokenVerifier verifier;

    /*public JsonWebTokenAuthFilter() {
    }*/

    @Context
    public void setServletContext(ServletContext aContext) throws JwkException {
        auth0Issuer     = Auth0Constants.AUTH0_ISSUER.getValue();
        logger.warn("Auth Issuer : " + auth0Issuer);
        logger.warn("creating JWTFactory");
        JWTFactory jwtFactory = new JWTFactory();
        logger.warn("created JWTFactory");
        //verifier = new JsonWebTokenVerifier(auth0Issuer, jwtFactory.getPublicKey());
    }

    @Override
    public void filter(ContainerRequestContext requestContext)
        throws IOException {

//      String authToken = getAuthToken(requestContext);

//      if (authToken.isEmpty()) {
//          logger.warn("JWT token missing.");
//          abortWithUnauthorized(requestContext);
//          return;
//      }

//      try {
//          if (validateToken(authToken) == false) {
//              logger.warn("abortWithUnauthorized invalid token");
//              abortWithUnauthorized(requestContext);
//          }

//      } catch (Exception e) {
//          logger.warn("abortWithUnauthorized exception ",e.getMessage());
//          abortWithUnauthorized(requestContext);
//      }
    }

//  private String getAuthToken(ContainerRequestContext requestContext) {
//      String token = getCookieAuthToken(requestContext);

//      if (token.isEmpty()){
//          token = getHeaderAuthToken(requestContext);
//      }
//      return token;
//  }

//  private String getCookieAuthToken(ContainerRequestContext requestContext) {

//      Map<String, Cookie> cookies  = (Map<String, Cookie>)requestContext.getCookies();
//      for (String key : cookies.keySet()){
//          logger.debug("Got cookie named "+ key);
//      }
//      logger.debug("Cookie map has " + cookies.size() );

//      String token = "";

//      Cookie jwtCookie = cookies.get("jwtToken");

//      // If the cookie is
//      if (jwtCookie != null) {
//          token = jwtCookie.getValue();
//      }
//      return token;
//  }

//  private String getHeaderAuthToken(ContainerRequestContext requestContext) {
//      String authorizationHeader =
//              requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

//      if (!isTokenBasedAuthentication(authorizationHeader)) {
//          logger.warn("JWT token not in header.");
//          return "";
//      }

//      String token = authorizationHeader
//                          .substring(AUTHENTICATION_SCHEME.length())
//                          .trim();
//      return token;
//  }


//  private void abortWithUnauthorized(ContainerRequestContext requestContext) {

//      logger.warn("Aborting with UNAUTHORIZED");
//      // Abort the filter chain with a 401 status code response
//      // The WWW-Authenticate header is sent along with the response
//      requestContext.abortWith(
//              Response.status(Response.Status.UNAUTHORIZED)
//                      .header(HttpHeaders.WWW_AUTHENTICATE,
//                              AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
//                      .build());
//  }

//  private boolean isTokenBasedAuthentication(String authorizationHeader) {

//      // Check if the Authorization header is valid
//      // It must not be null and must be prefixed with "Bearer" plus a
//      // whitespace The authentication scheme comparison must be
//      // case-insensitive
//      return authorizationHeader != null && authorizationHeader.toLowerCase()
//                  .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
//  }

//  private boolean validateToken(String token) throws Exception {
//      SecuredRestfulMethodHelper helper = new SecuredRestfulMethodHelper();

//      String[] requiredGroups = helper.getGroups(resourceInfo);

//      boolean isValid = verifier.isValidAccessRequest(token,
//              "https://gorticrum.com/user_authorization",
//                                                         requiredGroups);
//      logger.warn("Valid token ?" + isValid);
//      return isValid;
//  }
}
