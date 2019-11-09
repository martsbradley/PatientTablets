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
import javax.transaction.UserTransaction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

        TypedQuery<AuthUser> authQuery = entityManager.createQuery( "from AuthUser user " +
                                                         "where " +
                                                         "user.username = (?1) and" +
                                                         " user.passwordHash = (?2)",
                                                          AuthUser.class);
        Set<AuthGroup> groups = new HashSet<>();

        authQuery.setParameter(1, userName);
        authQuery.setParameter(2, hashedPassword);
        try {
            AuthUser user = authQuery.getSingleResult();
            Set<AuthUserGroup> authUserGroups = user.getGroups();

            for (AuthUserGroup authUserGroup : authUserGroups) {
                groups.add(authUserGroup.getGroup());
            }
        }
        catch (NoResultException e) {
            logger.warn(String.format("Authentication for user %s failed", userName));
            throw new AuthenticationException("Authentication failure");
        }
        return groups;
    }

  //public Medicine findById(long medicineId)
  //{
  //    logger.info("******findById***********");
  //    Medicine medicine = entityManager.find(Medicine.class, medicineId);

  //    logger.info("findById " + medicine);

  //    return medicine;
  //}
}
