package martinbradley.security;

import com.auth0.jwt.JWT;
import martinbradley.hospital.core.domain.password.AuthGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Model;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Set;

//  This is the high level class for creating a JWT
//  Handling the RSA signature
@Model
public class JWTFactory {

    private static final Logger logger = LoggerFactory.getLogger(JWTFactory.class);
    private String issuer = "";
    private String namespace ="";
    private final KeyPair keyPair;

    public JWTFactory() {

        this.issuer = "Martin Bradley"; // Load from environment.
        this.namespace = "mySite";      // Load from environment.
        keyPair = loadKeyStore();
    }

    private KeyPair loadKeyStore() {
        // FROM ENVIRONMENT ...
        String keyStorePath = "/home/martin/Software/Security/JavaKeytool/examplestore";
        char [] keyStorePassword = "abcdef".toCharArray();

        KeyPair keyPair = getKeyPair(keyStorePath, keyStorePassword);
        return keyPair;
    }

    public KeyPair getKeyPair(String keyStorePath, char[] keyStorePassword) {

        logger.info("Starting");

        try (FileInputStream is = new FileInputStream(keyStorePath)) {

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(is, keyStorePassword);

            String alias = "signFiles";

            Key key = keystore.getKey(alias, keyStorePassword);
            Arrays.fill(keyStorePassword, '0');

            if (key instanceof PrivateKey) {
                logger.info("Key is a private one " + key);
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(alias);

                // Get public key
                PublicKey publicKey = cert.getPublicKey();
                logger.info("Public Key is " + publicKey);

                // Return a key pair
                return new KeyPair(publicKey, (PrivateKey) key);
            } else {
                logger.warn("Cannot create keyPair from keystore :" + keyStorePath);
                throw new RuntimeException("Unable to load keystore from " + keyStorePath);
            }
        }
        catch(Exception e) {
            logger.warn("Cannot create keyPair from keystore :" + keyStorePath);
            logger.warn("Issue was ", e);
            throw new RuntimeException("Unable to load keystore from " + keyStorePath);
        }
    }

    public String signedJWT(Set<AuthGroup> groups) {
        return "";
    }

    public JWTString createJWT(Set<AuthGroup> groups) throws Exception {

        JWTString jwt = buildJWT(groups);

        return jwt;
    }

    private JWTString buildJWT(Set<AuthGroup> groups) throws Exception {
        JWTString.Builder builder = new JWTString.Builder();

        builder.setIssuer(issuer);

        String[] groupsArr = new String[groups.size()];
        int idx = 0;
        for (AuthGroup group: groups) {
            groupsArr[idx++] = group.getName();
        }

        builder.setGroups(namespace, groupsArr);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusMinutes(10);

        long issuedAt = toSinceEpoch(now);
        long expires  = toSinceEpoch(expiryTime);

        builder.setIat(issuedAt);
        builder.setExp(expires);

        JWTString jwt = builder.build();
        jwt.sign(keyPair);
        return jwt;
    }

    private long toSinceEpoch(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toEpochSecond();
    }
}

