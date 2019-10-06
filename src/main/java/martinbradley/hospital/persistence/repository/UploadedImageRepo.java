package martinbradley.hospital.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import martinbradley.hospital.core.domain.UploadedImage;
import javax.annotation.Resource;
import javax.ejb.TransactionManagement;
import javax.ejb.EJBException;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Model;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;

@Model
@TransactionManagement(TransactionManagementType.BEAN)
public class UploadedImageRepo {

    @PersistenceContext
    EntityManager entityManager;
    @Resource
    UserTransaction tx;
    private static final Logger logger = LoggerFactory.getLogger(UploadedImageRepo.class);

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public List<UploadedImage> getImages(long aPatientId) {

        logger.info("getImages "    + aPatientId);
        TypedQuery<UploadedImage> query = entityManager.createQuery(
                                     "SELECT i FROM UploadedImage i where patient.id = :patientId", 
                                     UploadedImage.class);

        query.setParameter("patientId", aPatientId);
        List<UploadedImage> list = query.getResultList();

        return list;
    }

    public boolean saveUpload(UploadedImage aUpload)
    {
        logger.debug("saveUpload " + aUpload);

        try{
            tx.begin();

            if (aUpload.getId() == null) {
                entityManager.persist(aUpload);
            }
            else {
                logger.warn("Cannot resave an image");
            }

            entityManager.flush();
            tx.commit();
        }
        catch (Exception e) {
            logger.warn("Error saving UploadedImage " + aUpload);

            rollbackTransaction();
            return false;
        }

        logger.info("uploadedimage saveUpload commit success");
        return true;
    }

    private void rollbackTransaction() {
        try{
            tx.rollback();
        }
        catch (Exception e) {
            logger.warn("Failed to rollback",e);
        }
    }
}
