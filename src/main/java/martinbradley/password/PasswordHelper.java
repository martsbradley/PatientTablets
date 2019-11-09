package martinbradley.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHelper {

    private static final Logger logger = LoggerFactory.getLogger(PasswordHelper.class);

    public String hashPassword(String password, String salt) {

        logger.debug("Hashing password");
        String originalString = concatPasswordAndSalt(password, salt);

        String sha256hex = makeHash(password, salt);

        return sha256hex;
    }

    private String concatPasswordAndSalt(String password, String salt) {
        return password + "|" + salt;
    }

    private String makeHash (String password, String salt) {

        String originalString = concatPasswordAndSalt(password, salt);
        MessageDigest digest = null;
        String hash = "FAILED";
        try {
            digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedhash = digest.digest(
                    originalString.getBytes(StandardCharsets.UTF_8));

            hash = bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            logger.warn("No such algorithm", e);
        }

        return hash;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }



}

