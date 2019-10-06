package martinbradley.auth0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.PublicKey;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.Jwk;
import java.util.concurrent.TimeUnit;
import com.auth0.jwk.JwkException;

/* This class works by givin the JwkProvider. The URL and the keyId.
 * The URL hosts a JSON object which has public keys.  Each public 
 * key is identified by a keyId.
 *
 */
public class Auth0KeyProvider {
    private static Logger logger = LoggerFactory.getLogger(Auth0KeyProvider.class);
    
    public final JwkProvider provider;
    
    public Auth0KeyProvider(String myURL) 
        throws JwkException {

        provider = new JwkProviderBuilder(myURL)
                                  .cached(10, 24,     TimeUnit.HOURS)
                                  .rateLimited(10, 1, TimeUnit.MINUTES)
                                  .build();
    }
    
    public PublicKey getPublicKey(String keyId) 
        throws JwkException {
        if (provider == null) {
            throw new IllegalStateException("Why is there no key");
        }

        Jwk jwk = provider.get(keyId);
        return jwk.getPublicKey();
    }
}
