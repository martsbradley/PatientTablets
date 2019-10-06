package martinbradley.auth0;

import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.auth0.jwk.JwkProvider;
import java.util.concurrent.TimeUnit;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;

public class Auth0RSASolution {
    private final Auth0Verifier verifier;
    private static Logger logger = LoggerFactory.getLogger(Auth0RSASolution.class);

    public Auth0RSASolution(String aURL,
                            String aIssuer)
        throws JwkException {
    
        Auth0KeyProvider provider = new Auth0KeyProvider(aURL);

        RSAPrivateKey privateKey = null;// Not needed, here we only verify tokens.

        verifier = new Auth0Verifier(aIssuer, 
                                     provider.getPublicKey(null)); 
    }

    public boolean isValidAccessRequest(String token, 
                                        String namespace,
                                        String ... groups) {

        return verifier.isValidAccessRequest(token, 
                                             namespace,
                                             groups);
    }
}
