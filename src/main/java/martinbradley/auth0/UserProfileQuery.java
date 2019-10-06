package martinbradley.auth0;

import com.auth0.net.Request;
import java.util.Map;
import com.auth0.json.auth.UserInfo;
import com.auth0.client.auth.AuthAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import com.auth0.exception.Auth0Exception;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.annotation.Resource;
import javax.transaction.UserTransaction;
import javax.transaction.SystemException;
import javax.enterprise.inject.Model;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

@Model
@TransactionManagement(TransactionManagementType.BEAN)
public class UserProfileQuery {

    private static Logger logger = LoggerFactory.getLogger(UserProfileQuery.class);

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserTransaction tx;

    public void handleGettingProfile(AuthAPI authAPI, 
                                     String jSWebToken,
                                     String groups) {

        boolean success = false;
        try {
            tx.begin();
            Request<UserInfo> userInfoRequest = authAPI.userInfo(jSWebToken);
            UserInfo userInfo = userInfoRequest.execute();
            Map<String, Object> userInfoMap = userInfo.getValues();

            for (String key : userInfoMap.keySet()) {

                logger.debug("UserInfo " + key + " => " + userInfoMap.get(key));
            }

            LoginDetail loginDetail = new LoginDetail();

            loginDetail.setName         ((String)userInfoMap.get("name"));
            loginDetail.setGivenName    ((String)userInfoMap.get("given_name"));
            loginDetail.setFamilyName   ((String)userInfoMap.get("family_name"));
            loginDetail.setNickname     ((String)userInfoMap.get("nickname"));
            loginDetail.setLocale       ((String)userInfoMap.get("locale"));
            loginDetail.setUpdatedAtTime((String)userInfoMap.get("updated_at"));
            loginDetail.setPicture      ((String)userInfoMap.get("picture"));
            loginDetail.setGroups       (groups);

            entityManager.persist(loginDetail);

            tx.commit();
            success = true;

        } catch(Auth0Exception e) {
            logger.warn("Issuer getting user profile " + e.getMessage());
        } catch(Exception e) {
            logger.warn("Exception in get profile " + e.getMessage());
        }
        finally {
            if (!success) {
                try {
                    tx.rollback();
                } catch (SystemException e) {
                    logger.warn("Could not roll back transaction " + e.getMessage());
                }
            }
        }
    }
}
