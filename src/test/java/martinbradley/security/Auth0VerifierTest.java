package martinbradley.auth0;

import martinbradley.security.JWTFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import java.time.ZoneId;
import martinbradley.security.JsonWebToken;

public class Auth0VerifierTest {
    private static final Logger logger = LoggerFactory.getLogger(Auth0VerifierTest.class);
    final KeyPair keyPair;
    final String issuer = "https://myeducation.eu.auth0.com/";
    JsonWebToken.Builder builder;

    Auth0VerifierTest() throws Exception {

        String keyStorePath = "/home/martin/Software/Security/JavaKeytool/examplestore";
        char [] keyStorePassword = "abcdef".toCharArray();

        JWTFactory factory = new JWTFactory();
        keyPair = factory.getKeyPair(keyStorePath,keyStorePassword);
    }

    @BeforeEach
    public void beforeAll() {
        createBuilder();
    }

    /* Creates a build tht by default will build
     * a valid JWT token */
    private JsonWebToken.Builder createBuilder() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hourBefore = now.minusHours(1);
        LocalDateTime hourAfter  = now.plusHours(1);

        long issuedAt = toSinceEpoch(hourBefore);
        long expires  = toSinceEpoch(hourAfter);
        System.out.println("Issued at " + issuedAt);

        builder = new JsonWebToken.Builder();
        builder.setIssuer(issuer)
               .setIat(issuedAt)
               .setExp(expires)
               .setScope("openid profile email read:patients");
        return builder;
    }

    private JsonWebToken createJWT() throws Exception {

        JsonWebToken jsonWebToken = builder.build();
        String header = jsonWebToken.getHeader();
        logger.info("Header is " + header);
        String payload = jsonWebToken.getPayload();
        logger.info("Payload is " + payload);

        jsonWebToken.sign(keyPair);
        return jsonWebToken;
    }

    private JsonWebToken createExpiredJWT() throws Exception {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hourBefore = now.minusHours(100);
        LocalDateTime hourAfter  = now.minusHours(1);// finished one hour ago!

        long issuedAt = toSinceEpoch(hourBefore);
        long expires  = toSinceEpoch(hourAfter);

        logger.warn("Issued at " + issuedAt);
        builder.setIat(issuedAt) 
               .setExp(expires);

        return createJWT();
    }


    private JsonWebToken createValidJWT() throws Exception {

        return createJWT();
    }

    private long toSinceEpoch(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond();
    }

    private Auth0Verifier createVerifier() {
        RSAPublicKey pub = (RSAPublicKey)keyPair.getPublic();

        Auth0Verifier auth = new Auth0Verifier(issuer, pub);
        return auth;
    }

    private void validate(String aIssuer, 
                          boolean expectedResult,
                          String ...aScopes) 
        throws Exception {

        JsonWebToken validJWT = createValidJWT();

        RSAPublicKey pub = (RSAPublicKey)keyPair.getPublic();

        Auth0Verifier auth = new Auth0Verifier(aIssuer, pub);
        boolean isValid = auth.validTokenHasScopes(validJWT.toString(), aScopes);
        assertThat(isValid, is(expectedResult));
    }

    @Test
    public void testExpiredJwt() throws Exception {
        JsonWebToken validJWT = createExpiredJWT();

        Auth0Verifier auth = createVerifier();
        boolean isValid = auth.validTokenHasScopes(validJWT.toString(), "read:patients");
        assertThat(isValid, is(false));
    }

    @Test
    public void testWrongIssuerJwt() throws Exception {
        String otherIssuer = "SomeOtherIssuer";

        validate(otherIssuer, false, "read:patients");
    }

    @Test
    public void tokenIsValid() throws Exception {
        JsonWebToken validJWT = createValidJWT();

        Auth0Verifier auth = createVerifier();

        boolean isValid = auth.tokenIsValid(validJWT.toString());
        assertThat(isValid, is(true));
    }

    @Test
    public void tokenIsNotValid() throws Exception {
        String invalidJWT = "This not a valid token";

        Auth0Verifier auth = createVerifier();

        boolean isValid = auth.tokenIsValid(invalidJWT);
        assertThat(isValid, is(false));
    }

    @Test
    public void validAccessRequest() throws Exception {

        builder.setGroups("namespace", "admin");

        JsonWebToken validJWT = createJWT();

        Auth0Verifier auth = createVerifier();
        boolean isValid = auth.isValidAccessRequest(validJWT.toString(), "namespace", "admin");
        assertThat(isValid, is(true));
    }

    @Test
    public void inValidAccessRequest() throws Exception {

        builder.setGroups("namespace", "non-admin");

        JsonWebToken validJWT = createJWT();

        Auth0Verifier auth = createVerifier();
        boolean isValid = auth.isValidAccessRequest(validJWT.toString(), "namespace", "admin");
        assertThat(isValid, is(false));
    }
    @Test
    public void groupsSetIncorrectly() throws Exception {

        builder.setGroups("namespace", "normal");

        JsonWebToken validJWT = createJWT();

        Auth0Verifier auth = createVerifier();
                                       // Missing the groups on the request
        boolean isValid = auth.isValidAccessRequest(validJWT.toString(), "namespace");
        assertThat(isValid, is(false));
    }

    @Test
    public void testJwtBlankScope() throws Exception {
        validate(issuer, false, "");
    }

    @Test
    public void testValidJwtNoScopes() throws Exception {
        validate(issuer, false);
    }

    @Test
    public void testValidJwt() throws Exception {
        validate(issuer, true, "read:patients");
    }

    @Test
    public void testValidJwtTwoScopes() throws Exception {
        validate(issuer, true, "openid", "read:patients");
    }

    @Test
    public void testReadGroups() throws Exception {
        builder.setGroups("namespace", "admin");

        JsonWebToken validJWT = createJWT();

        Auth0Verifier auth = createVerifier();
        Set<String> groups = auth.readGroups(validJWT.toString(), "namespace");
        assertThat(groups.size(), is(1));
        assertThat(groups.contains("admin"), is(true));
    }
    @Test
    public void testReadNoGroups() throws Exception {

        JsonWebToken validJWT = createJWT();

        Auth0Verifier auth = createVerifier();
        Set<String> groups = auth.readGroups(validJWT.toString(), "namespace");
        assertThat(groups.size(), is(0));
    }
    @Test
    public void testReadThreeGroups() throws Exception {

        builder.setGroups("namespace", "admin","controllers", "wasters");
        JsonWebToken validJWT = createJWT();

        Auth0Verifier auth = createVerifier();
        Set<String> groups = auth.readGroups(validJWT.toString(), "namespace");

        assertThat(groups.size(), is(3));
        assertThat(groups.contains("admin"),       is(true));
        assertThat(groups.contains("controllers"), is(true));
        assertThat(groups.contains("wasters"),     is(true));
    }

    @Test
    public void testReadGroupsInvalid() throws Exception {
        assertThrows(Exception.class,
                () -> {
            String inValidJWT = "This not a valid token";

            Auth0Verifier auth = createVerifier();
            Set<String> groups = auth.readGroups(inValidJWT, "namespace");
        });
    }
}