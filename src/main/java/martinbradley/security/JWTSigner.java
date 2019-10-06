package martinbradley.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import static java.util.Base64.Encoder;
import static java.util.Base64.Decoder;
import java.security.KeyPair;
import java.security.Signature;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;
import java.util.Iterator;
import java.util.Arrays;

    //data = base64urlEncode( header ) + “.” + base64urlEncode( payload
public class JWTSigner {

    private static final Logger logger = LoggerFactory.getLogger(JWTSigner.class);
    private final KeyPair keyPair;
    private String unencodedHeader = "";
    private String unencodedPayload = "";

    public JWTSigner(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public void setHeader(String unencodedHeader) {
        this.unencodedHeader = unencodedHeader;
    }
    public void setPayload(String unencodedPayload) {
        this.unencodedPayload = unencodedPayload;
    }

    public String createSignedJWT() 
        throws Exception {
        logger.info("header is  "+ unencodedHeader);
        logger.info("payload is  "+ unencodedPayload);


        String encodedHeader  = encode(unencodedHeader);
        String encodedPayload = encode(unencodedPayload);

        System.out.println("encodedHeader " + encodedHeader);
        System.out.println("encodedPayload " + encodedPayload);

        String toBeSigned = encodedHeader + "." + encodedPayload;
        String signature = signIt(toBeSigned);
        String jwt = toBeSigned + "." + signature;
        logger.debug("Result is "+ jwt);
        return jwt;
    }

    private String signIt(String aToBeSigned) 
        throws Exception {
        try {
            Signature signer = Signature.getInstance("SHA256withRSA");

            signer.initSign(this.keyPair.getPrivate());
            signer.update(aToBeSigned.getBytes());
            byte[] signedBytes = signer.sign();

            Encoder encoder = Base64.getEncoder();
            byte[] encodedBytes = encoder.encode(signedBytes);

            String signature = new String(encodedBytes);
            return signature;
        } catch (NoSuchAlgorithmException e) {
            logger.warn("No such signature", e);
            throw e;
        }
        catch (Exception e) {
            logger.warn("Error ", e);
            throw e;
        }
    }

    private String encode(String aInput) {
        Encoder encoder = Base64.getEncoder();

        byte[] encodedBytes = encoder.encode(aInput.getBytes());

        String output = new String(encodedBytes);
        return output;
    }

  //private static class Joiner {
  //    StringJoiner joiner = new StringJoiner(",", "{", "}");

  //    public String create(Object ... args) {

  //        Iterator<Object> it = Arrays.asList(args).iterator();

  //        while (it.hasNext()) {
  //            String key = (String)it.next();
  //            Object value = it.next();

  //            String item = getJWTSource(key, value);
  //            joiner.add(item);
  //        }
  //        return joiner.toString();
  //    }

  //    private String getJWTSource(String key, Object value) {

  //        if (value instanceof String) {
  //            String valStr = (String)value;

  //            if (valStr.indexOf("{") == 0) {
  //                // handle objects
  //                return String.format("\"%s\":%s", key,value);
  //            }
  //            else {
  //                return String.format("\"%s\":\"%s\"", key,value);
  //            }
  //        }
  //        else {
  //            return String.format("\"%s\":%d", key, value);
  //        }
  //    }
  //}
}
