package martinbradley.auth0;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.PublicKey;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwk.JwkException;

public class Auth0KeyProviderTest {
    private static Logger logger = LoggerFactory.getLogger(Auth0KeyProviderTest.class);

    @Test
    public void validKey() 
        throws Exception {
        String url = "https://myeducation.eu.auth0.com";
        String key  = "RjZDMzYyQTBBMzMxRTAyODIzOEIxRjBCRUNBMzE4M0ZGNTE0MTNGQQ";

        Auth0KeyProvider provider = new Auth0KeyProvider(url);
        PublicKey publicKey = provider.getPublicKey(key);

        assertThat(publicKey, is(notNullValue()));
    }

    @Test
    public void unknownKeyAfterConstruction() {
        assertThrows(JwkException.class,
                () -> {
            String url = "https://myeducation.eu.auth0.com";
            String key  = "RjZDMzYyQTBBMzMxRTAyODIzOEIxRjBCRUNBMzE4M0ZGNTE0MTNGQQ";

            Auth0KeyProvider provider = new Auth0KeyProvider(url);
            String unknownKey  = "unknown";
            provider.getPublicKey(unknownKey);
        });
    }
    @Test
    public void keyNull() 
        throws Exception {
      //assertThrows(JwkException.class,
      //        () -> {
            String url = "https://myeducation.eu.auth0.com";
            String key  = "RjZDMzYyQTBBMzMxRTAyODIzOEIxRjBCRUNBMzE4M0ZGNTE0MTNGQQ";

            Auth0KeyProvider provider = new Auth0KeyProvider(url);
            String nullKey  = null;
            Object x = provider.getPublicKey(nullKey);
            assertThat(x, is(notNullValue()));
            System.out.println(x);
      //});
    }
}
