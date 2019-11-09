package martinbradley.security;
import java.util.StringJoiner;
import java.util.Arrays;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JWTString {
    private final String issuer;
    private final long iat;
    private final long exp;
    private final String scope;
    private final String namespace;
    private final String[] groups;  
    private static final Logger logger = LoggerFactory.getLogger(JWTString.class);

    private JWTString(Builder aBuilder) {
        issuer    = aBuilder.issuer;
        iat       = aBuilder.iat;
        exp       = aBuilder.exp;
        scope     = aBuilder.scope;
        namespace = aBuilder.namespace;
        groups    = aBuilder.groups;
    }

    public String getHeader() {
        JSONObject header = new JSONObject();
        header.put("alg", "RS256");
        header.put("typ", "JWT");

        return header.toString();
    }
    public String getPayload() {

        logger.debug("**Building Payload");
        JSONObject payload = new JSONObject();
        payload.put("sub","1234567890");
        payload.put("name", "Martin Bradley");
        payload.put("iss", issuer);
        payload.put("scope", scope);
        payload.put("iat", new Long(iat));
        payload.put("exp", new Long(exp));

        logger.debug("payload" + payload.toString());

        if (namespace != null && groups != null) {
            JSONArray groupsArray = new JSONArray(groups);
            JSONObject group = new JSONObject();

            group.put("groups",groupsArray);

            payload.put(namespace, group);
        }

        String strPayload = payload.toString();
        logger.debug("payload done " + strPayload);

        return strPayload;
    }

    public static class Builder {
        String issuer;
        long iat;
        long exp;
        String scope;
        String namespace;
        String[] groups;

        public Builder setIssuer(String aIssuer) {
            this.issuer = aIssuer;
            return this;
        }
        public Builder setIat(long aIat) {
            this.iat = aIat;
            return this;
        }
        public Builder setExp(long aExp){
            this.exp = aExp;
            return this;
        }
        public Builder setScope(String aScope){
            this.scope = aScope;
            return this;
        }
        public Builder setGroups(String namespace, String ... groups) {
            this.namespace = namespace;
            this.groups = groups;
            return this;
        }
        public JWTString build(){
            return new JWTString(this);
        }
    }
}
