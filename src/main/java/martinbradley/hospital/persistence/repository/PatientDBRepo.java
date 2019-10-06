package martinbradley.hospital.persistence.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import javax.annotation.Resource;
import javax.ejb.EJBException;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Model;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import javax.transaction.Status;
import javax.persistence.FlushModeType;
import javax.persistence.EntityTransaction;
import martinbradley.hospital.core.api.dto.*;
import martinbradley.hospital.core.message.MessageKeyImpl;
import martinbradley.hospital.core.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityGraph;
import javax.persistence.OptimisticLockException;


@Model
@TransactionManagement(TransactionManagementType.BEAN)
public class PatientDBRepo 
{
    @PersistenceContext
    EntityManager entityManager;

    @Resource
    UserTransaction tx;

    private static final Logger logger = LoggerFactory.getLogger(PatientDBRepo.class);

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    public void deletePatient(Patient aPatient)
    {
        logger.info("Deleting patient ");
        logger.debug("patient details :" + aPatient);
        try{
            tx.begin();

            if (!entityManager.contains(aPatient))
            {
                logger.info("Calling merge");
                aPatient = entityManager.merge(aPatient);
            }
            Set<Long> tabletIds = new HashSet<>();

            /* JPA will deleting prescriptions one by one when the patient 
             * is deleted.  To avoid that get all prescriptions and 
             * delete them with one statement.
             * Then update the patient entity to have no prescriptions.
             */
            for (Prescription tablet: aPatient.getPrescription())
            {
                tabletIds.add(tablet.getId());
                logger.info("Need to delete tablet " + tablet.getId());
            }

            Query qDeleteVisitors = entityManager.createQuery("delete from Prescription obj " +
                                                              "where obj.id in (?1)");
            qDeleteVisitors.setParameter(1, tabletIds);
            qDeleteVisitors.executeUpdate();

            aPatient.setPrescription(Collections.emptyList());

            logger.debug("calling remove");
            entityManager.remove(aPatient);

            logger.info("committed");
            tx.commit();
        }
        catch (Exception e)
        {
            rollbackTransaction();
            logger.warn("error " + e.getMessage());
        }
    }




    private String getTransactionStatusString() throws Exception {

        switch (tx.getStatus()) {
            case Status.STATUS_ACTIVE:           return "Status.STATUS_ACTIVE";   
            case Status.STATUS_COMMITTED:        return "Status.STATUS_COMMITTED";   
            case Status.STATUS_COMMITTING:       return "Status.STATUS_COMMITTING";   
            case Status.STATUS_MARKED_ROLLBACK:  return "Status.STATUS_MARKED_ROLLBACK";   
            case Status.STATUS_NO_TRANSACTION:   return "Status.STATUS_NO_TRANSACTION";   
            case Status.STATUS_PREPARED:         return "Status.STATUS_PREPARED";   
            case Status.STATUS_PREPARING:        return "Status.STATUS_PREPARING";   
            case Status.STATUS_ROLLEDBACK:       return "Status.STATUS_ROLLEDBACK";   
            case Status.STATUS_ROLLING_BACK:     return "Status.STATUS_ROLLING_BACK";   
            case Status.STATUS_UNKNOWN:          return "Status.STATUS_UNKNOWN";   
            default: return "Status Unknown";
      } 
    }

    public SavePatientResponse savePatient(Patient aPatient)
    {
        aPatient.setSex(Sex.Male);
      //logger.info("\n\n\n\n\nSave patient :" + aPatient);
      //logger.info("Transacion is " + tx);
      //logger.info("entityManager is " + entityManager);
        //FlushModeType flushMode =  entityManager.getFlushMode();
        //logger.info("FlushModeType is " + flushMode);

        SavePatientResponse resp = null;
        MessageCollection msg = new MessageCollection();
        try{
            //logger.warn("Tx status is " + getTransactionStatusString());
            tx.begin();
            //logger.warn("After begin Tx status is " + getTransactionStatusString());

            PatientRepoHelper helper = new PatientRepoHelper();

            if (helper.duplicatePatientCheck(entityManager, aPatient)) {
                logger.info("This patient already exists!");
                msg.add(new Message(MessageKeyImpl.PATIENT_NAME_DUPLICATE));

                resp = new SavePatientResponse(aPatient, msg);
                return resp;
            }

            logger.info("Patient not a duplicate");

            if (aPatient.getId() == null) {
                int totalPatients = getTotalPatients();
                logger.info("Got totalPatients " + totalPatients);

                if (totalPatients < 50) {
                    logger.info("Calling persist");

                    entityManager.persist(aPatient);
                }
                else {
                    throw new Exception("Already enough patients " + totalPatients);
                }
            }
            else {
                logger.info("Calling merge");
                entityManager.merge(aPatient);
            }

            logger.info("save finished");
            entityManager.flush();
            tx.commit();
            //logger.warn("After commit tx status is " + getTransactionStatusString());
            resp = new SavePatientResponse(aPatient, msg);
        }
        catch (OptimisticLockException e) {
            logger.warn("OptimisticLockException " + e.getMessage());

            msg.add(new Message(MessageKeyImpl.OPTIMISTIC_LOCK_EXCEPTION));

            resp = new SavePatientResponse(aPatient, msg);

            rollbackTransaction();
            return resp;
        }
        catch (Exception e) {
            logger.warn("Error saving Patient " + e.getClass().getName());

            rollbackTransaction();

            msg.add(new Message(MessageKeyImpl.OPTIMISTIC_LOCK_EXCEPTION));// fix me here!!!

            resp = new SavePatientResponse(aPatient, msg);
        }

        logger.info("savePatient returning " + aPatient.getId());
        return resp;
    }

    private void rollbackTransaction() {
        try{
            tx.rollback();
        }
        catch (Exception e) {
            logger.warn("Failed to rollback",e);
        }
    }


    public List<Patient> pagePatients(int start, int pageSize,
                                      Patient.SortOrder order)
    {
        logger.info("pagePatients called with " + start);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Patient> criteriaQuery = criteriaBuilder.createQuery(Patient.class);
        Root<Patient> from = criteriaQuery.from(Patient.class);
        CriteriaQuery<Patient> select = criteriaQuery.select(from);

        criteriaQuery.orderBy(order.getOrder(criteriaBuilder, from));

        TypedQuery<Patient> query  = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);

        List<Patient> patients = query.getResultList();

        for (Patient p : patients)
        {
            p.setPrescription(Collections.emptyList());
        }
        return patients;
    }


    public Patient loadById(long id)
    {
        Patient pat = null;
      //try
        {
            //tx.begin();
            EntityGraph graph = entityManager.getEntityGraph("graph.Patient.prescriptions");
              
            Map<String,Object> hints = new HashMap<>();
            hints.put("javax.persistence.loadgraph", graph);

            pat = entityManager.find(Patient.class, id, hints);

            logger.debug("loadById " + pat);
            //tx.commit();
        }
      //catch (Exception e)
      //{
      //    logger.warn(e.getMessage());
      //}
      //finally {
      //    logger.warn("loadById finally");
      //}
        return pat;
    }

    public int getTotalPatients() {
        PatientRepoHelper helper = new PatientRepoHelper();

        int totalPatients = helper.getTotalPatients(entityManager);
        return totalPatients;
    }
}
