package martinbradley.hospital.persistence.repository;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import martinbradley.hospital.core.domain.*;
import martinbradley.hospital.core.domain.SavePatientResponse;
import mockit.*;
import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.CoreMatchers.not;
import javax.persistence.EntityTransaction;

public class PatientDBRepoTestIT
{
    @Injectable EntityManager entityManager;
    @Injectable UserTransaction tx;
    @Mocked PatientRepoHelper helper;
    @Tested PatientDBRepo impl;

    @Test
    @SuppressWarnings("unchecked")
    public void testOne(@Mocked Patient.SortOrder order)
    {
        new Expectations(){{
            order.getOrder((CriteriaBuilder)any, (Root<Patient>)any);
        }};

        List<Patient> result = impl.pagePatients(0, 1, order);
    }

    @Test
    public void saveNotReturnNull(@Mocked Patient patient)
    {
        SavePatientResponse response = impl.savePatient(patient);
        assertThat(response, is(notNullValue()));
    }

    @Test
    public void can_resave_patient_over_again(@Mocked TypedQuery playerQuery)
    {
        Patient patient = new Patient();
        patient.setId(1L);

        Patient existingPatient = new Patient();
        existingPatient.setId(1L);

        mockCheckDuplicateCall(false);

        SavePatientResponse response = impl.savePatient(patient);

        boolean thereAreErrors = response.getMessages().iterator().hasNext();
        assertThat(thereAreErrors, is(not(true)));
    }

    private void mockCheckDuplicateCall(final boolean isDuplicate)
    {
        new Expectations() {{   
            helper.duplicatePatientCheck((EntityManager)any, (Patient)any);
            result = isDuplicate;
        }};
    }

    @Test
    public void cannot_save_duplicate_patient(@Mocked TypedQuery playerQuery)
    {
        Patient patient = new Patient();
        patient.setId(1L);

        Patient existingPatient = new Patient();
        existingPatient.setId(4L);

        mockCheckDuplicateCall(true);

        SavePatientResponse response = impl.savePatient(patient);

        boolean thereAreErrors = response.getMessages().iterator().hasNext();
        assertThat(thereAreErrors, is(true));
    }

    @Test void cannot_patient_too_many_patients(@Mocked TypedQuery playerQuery) {
        Patient patient = new Patient();

        mockCheckDuplicateCall(false);

        new Expectations(){{   
            helper.getTotalPatients((EntityManager)any);
            result = 151;
        }};

        SavePatientResponse response = impl.savePatient(patient);

        boolean thereAreErrors = response.getMessages().iterator().hasNext();
        assertThat(thereAreErrors, is(true));
    }

    @Test
    public void save_prescriptions(@Mocked TypedQuery playerQuery)
    {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setForename("martin");
        patient.setSurname("martin");

        Prescription p = new Prescription();
        p.setAmount("amount");

        patient.getPrescription().add(p);

        SavePatientResponse response = impl.savePatient(patient);
    }
}
