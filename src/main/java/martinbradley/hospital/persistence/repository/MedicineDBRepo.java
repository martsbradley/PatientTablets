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
import martinbradley.hospital.core.domain.Medicine;
import martinbradley.hospital.core.domain.Sex;
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
public class MedicineDBRepo {

    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    UserTransaction tx;
    private static final Logger logger = LoggerFactory.getLogger(MedicineDBRepo.class);

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    public List<Medicine> listMedicines() {
        logger.info("Find find all menuitems!!!!!!!!");
        //Session session = sessionFactory.getCurrentSession();
        //Query itemsQuery = session.createQuery("from Medicine p order by p.patientId");
        //List<Medicine> items = new ArrayList<>();
        Query query = entityManager.createQuery("SELECT e FROM Medicine e");
	@SuppressWarnings("unchecked")
        List<Medicine> list =  (List<Medicine>)query.getResultList();
        return list;
    }


    public List<Medicine> pageMedicines(int start,
                                        int pageSize,
                                        Medicine.SortOrder order,
                                        String filter)
    {
        logger.info("pageMedicines called start "    + start);
        logger.info("pageMedicines called pageSize " + pageSize);
        logger.info("pageMedicines called order "    + order);
        logger.info("pageMedicines called filter "   + filter);

        // First create a CriteriaQuery
        // Then convert it to a typed query since that TypedQuery interface has
        // the paging methods.
        CriteriaBuilder cBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Medicine> criteriaQuery = cBuilder.createQuery(Medicine.class);

        Root<Medicine>          from   = criteriaQuery.from(Medicine.class);
        CriteriaQuery<Medicine> select = criteriaQuery.select(from);

        criteriaQuery.orderBy(order.getOrder(cBuilder, from));

        if (filter != null && !filter.isEmpty())
        {
            logger.info("name predicate active '" + filter + "'");
            Predicate pred = cBuilder.like(cBuilder.lower(from.get("name")),"%" + filter + "%");
            criteriaQuery.where(pred );
        }

          
        TypedQuery<Medicine> query  = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(start);
        query.setMaxResults(pageSize);
          
        List<Medicine> list = query.getResultList();
        return list;
    }

    public int getTotalMedicines(String filter)
    {
        logger.info("getTotalMedicines called '" + filter + "'");
        CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Long> criteriaQuery = queryBuilder.createQuery(Long.class);

        Root<Medicine>          from   = criteriaQuery.from(Medicine.class);

        criteriaQuery.select(queryBuilder.count(from));

        if (filter != null && !filter.isEmpty())
        {
            logger.info("getTotalMedicines name predicate active " + filter);
            Predicate pred = queryBuilder.like(
                             queryBuilder.lower(from.get("name")),
                                                "%" + filter + "%");
            criteriaQuery.where(pred);
        }

        TypedQuery<Long> query  = entityManager.createQuery(criteriaQuery);
        Long result = query.getSingleResult();

        return result.intValue();
    }

    public List<Medicine> searchMedicine(String queryString)
    {
        TypedQuery<Medicine> query = entityManager.createNamedQuery("Medicine.similarNameQuery",
                                                                     Medicine.class);
        query.setParameter("querystring", "%" + queryString + "%");

        List<Medicine> sameNamedMedicines = query.getResultList();
        return sameNamedMedicines;

    }
    
    public Medicine findById(long medicineId)
    {
        logger.info("******findById***********");
        Medicine medicine = entityManager.find(Medicine.class, medicineId);

        logger.info("findById " + medicine);

        return medicine;
    }

  //public long saveMedicine(Medicine aMedicine)
  //{
  //    aMedicine.setSex(Sex.Male);
  //    aMedicine.setDob(LocalDate.now());
  //    logger.info("Save called :" + aMedicine);
  //    try{
  //        tx.begin();

  //        entityManager.persist(aMedicine);
  //        logger.info("save returning " + aMedicine.getMedicineId());
  //        tx.commit();
  //    }
  //    catch (Exception e)
  //    {
  //        logger.info("error saving Medicine " + e.getMessage());
  //    }
  //    return aMedicine.getMedicineId();
  //}
//  /*@Override
//  public boolean updateMedicine(Medicine aMedicine)
//  {
//          logger.info("******updateMedicine***********");
//          Session session = sessionFactory.getCurrentSession();
//          session.update(aMedicine);

//          /*  This queries patient
//           * 
//           * 
//           */
//          //Query itemsQuery = session.createQuery("from Medicine");
//          //List<Medicine> patients = itemsQuery.list();
//          /*
//          aMedicine = (Medicine)session.load(Medicine.class, aMedicine.getMedicineId());

//          
//      logger.info(aMedicine.getForename() + " has " + aMedicine.getPrescription().size() + " drugs.");
//      for (Prescription drug: aMedicine.getPrescription())
//      {
//          logger.info("Medicine has drugs "+ aMedicine.getForename());
//          logger.info("drug " + drug.getId());
//      }

//          logger.info("*****************\nloading my patient");
//          Prescription drug = (Prescription)session.load(Prescription.class, (long)2);
//          logger.info("Got drug id "+ drug.getId() + " with patient " + drug.getMedicine());
//          */
//      return true;
//  }


//  @Override
//  public List<Prescription> getPrescriptions(long patientId)
//  {
//          logger.info("getPrescriptions");
//          Session session = sessionFactory.getCurrentSession();
//          Query itemsQuery = session.createQuery("from Prescription where patientId = :id ");
//          itemsQuery.setParameter("id", patientId);
//          List<Prescription> items = (List<Prescription>)itemsQuery.list();
//      return items;
//  }

//  //@Override
//  public long saveMedicine(Medicine prescription)
//  {
//      logger.info("SaveMedicine ");
//      Session session = sessionFactory.getCurrentSession();
//      session.save(prescription);
//          return prescription.getId();
//  }
}
