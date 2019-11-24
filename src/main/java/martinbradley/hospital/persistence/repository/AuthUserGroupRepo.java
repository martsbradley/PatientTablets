package martinbradley.hospital.persistence.repository;

import martinbradley.hospital.core.domain.password.AuthGroup;
import martinbradley.hospital.core.domain.password.AuthUser;
import martinbradley.hospital.core.domain.password.AuthUserGroup;
import martinbradley.hospital.core.domain.password.Salt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Model;
import javax.naming.AuthenticationException;
import javax.persistence.*;
import javax.transaction.*;
import javax.transaction.RollbackException;
import java.util.*;

@Model
@TransactionManagement(TransactionManagementType.BEAN)
public class AuthUserGroupRepo {

    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    UserTransaction tx;
    private static final Logger logger = LoggerFactory.getLogger(AuthUserGroupRepo.class);

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }



    public Salt getUserSalt(String userName)
        throws AuthenticationException
    {
        TypedQuery<String> query = entityManager.createNamedQuery("User.queryUserSalt",
                                                                     String.class);
        query.setParameter("username", userName);

        Salt salt = null;
        try {
            salt = new Salt(query.getSingleResult());
        }
        catch (NoResultException e) {
            throw new AuthenticationException("Authentication failure");
        }
        return salt;
    }

    public Set<AuthGroup> authenticate(String userName, String hashedPassword)
        throws AuthenticationException {

        logger.info(String.format("Auth Database check for user %s", userName));


        TypedQuery<AuthUser> authQuery = entityManager.createQuery( "from AuthUser user " +
                                                         "where " +
                                                         "user.username = (?1) and" +
                                                         " user.passwordHash = (?2)",
                                                          AuthUser.class);

        EntityGraph graph = entityManager.getEntityGraph("graph.AuthUser.prescriptions");

        authQuery.setHint("javax.persistence.loadgraph", graph);

        Set<AuthGroup> groups = new HashSet<>();

        authQuery.setParameter(1, userName);
        authQuery.setParameter(2, hashedPassword);
        try {
            tx.begin();
            logger.info(String.format("Calling query for user %s", userName));
            AuthUser user = authQuery.getSingleResult();
            Set<AuthUserGroup> authUserGroups = user.getAuthUserGroups();

            logger.info(String.format("Got Groups user %s", userName));
            for (AuthUserGroup authUserGroup : authUserGroups) {
                groups.add(authUserGroup.getGroup());
            }
            logger.info(String.format("Loaded groups user %s", userName));
            tx.commit();
        }
        catch (NoResultException e) {
            logger.warn(String.format("Authentication for user %s failed", userName));

            rollback(e);
            throw new AuthenticationException("Authentication failure");
        } catch (Exception e) {
            rollback(e);
            logger.warn("Txn Rollback ",e);
        }
        logger.info(String.format("Authentication for user %s returning %d groups", userName, groups.size()));
        return groups;
    }

    private void rollback(Exception e) {
        try {
            tx.rollback();
        } catch (Exception ex) {
            logger.warn("Rollback failed ",e);
        }
    }

    //public Medicine findById(long medicineId)
  //{
  //    logger.info("******findById***********");
  //    Medicine medicine = entityManager.find(Medicine.class, medicineId);

  //    logger.info("findById " + medicine);

  //    return medicine;
  //}
}
