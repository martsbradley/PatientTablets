package martinbradley.security;

import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSASigner {

    private static final Logger logger = LoggerFactory.getLogger(RSASigner.class);

    public KeyPair getKeyPair() throws Exception {
        String keytool = "/home/martin/Software/Security/JavaKeytool/examplestore";
        String keyStorePassword = "abcdef";
        
        logger.info("Starting");

        FileInputStream is = new FileInputStream(keytool);

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(is, keyStorePassword.toCharArray());

        String alias = "signFiles";

        Key key = keystore.getKey(alias, keyStorePassword.toCharArray());

        if (key instanceof PrivateKey) {
            logger.info("Key is a private one "+ key);
            // Get certificate of public key
            Certificate cert = keystore.getCertificate(alias);

            // Get public key
            PublicKey publicKey = cert.getPublicKey();
            logger.info("Public Key is " + publicKey);

            // Return a key pair
            return new KeyPair(publicKey, (PrivateKey) key);
        }
        else {
            throw new Exception("Could not create the key");
        }
    }
}
