
package martinbradley.hospital.persistence.repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;
import martinbradley.hospital.core.domain.*;

class PatientRepoHelper {

    private static final Logger logger = LoggerFactory.getLogger(PatientRepoHelper.class);
    public int getTotalPatients(EntityManager entityManager)
    {
        logger.warn("Real PatientRepoHelper called");
        Query query = entityManager.createQuery("select COUNT(m) from Patient m");

        logger.debug("totalQuery got " + query);
        Number result = (Number) query.getSingleResult();
        logger.debug("totalQuery getSingleResult " + result);

        return result.intValue();
    }

    public boolean duplicatePatientCheck(EntityManager entityManager, Patient aPatient)
    {
        TypedQuery<Patient> query = entityManager.createNamedQuery("Patient.withSameName",
                                                                   Patient.class);
        query.setParameter("forename",aPatient.getForename());
        query.setParameter("surname", aPatient.getSurname());

        List<Patient> sameNamedPatients= query.getResultList();

        boolean duplicate = !sameNamedPatients.isEmpty() && 
                            (sameNamedPatients.size() != 1 ||
                             !sameNamedPatients.get(0).getId().equals(aPatient.getId()));

      //logger.warn("Found a duplicate..?" + duplicate);
      //logger.warn("Patient is " + aPatient);
      //logger.warn("This list contains "+ sameNamedPatients);

        return duplicate;
    }
}
