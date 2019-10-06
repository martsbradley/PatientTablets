package martinbradley.hospital.core.api;
import martinbradley.hospital.core.api.dto.MessageCollection;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.core.domain.Prescription;
import martinbradley.hospital.core.domain.Sex;
import mockit.*;
import martinbradley.hospital.persistence.repository.PatientDBRepo;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNot.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import static martinbradley.hospital.core.beans.PageInfo.PageInfoBuilder;
import martinbradley.hospital.core.beans.PageInfo;

public class PatientBrokerImplTest
{
    @Mocked PatientDBRepo mockRepo;
    private PatientBrokerImpl impl;

    @BeforeEach
    public void setMeUp()
    {
        impl = new PatientBrokerImpl();
        impl.repo = mockRepo;
    }

    @Test
    public void getPatients_CallsRepo() {
        Patient p1 = new Patient();
        Patient p2 = new Patient();
        final List<Patient> myResults = Arrays.asList(p1,p2);
    
        new Expectations(){{
            mockRepo.pagePatients(1, 10, (Patient.SortOrder)any); result = myResults;
        }};

        final String sortField = "forename";

        PageInfo pageInfo  = new PageInfoBuilder()
                                 .setStartAt(1)
                                 .setMaxPerPage(10)
                                 .setSortField(sortField)
                                 .setIsAscending(true)
                                 .build();

        List<Patient> list = impl.getPatientsPaged(pageInfo);
        assertThat(list, is(not(empty())));
    }

    @Test
    public void savePatient_CallsRepo() {
        Patient pat = new Patient();
        pat.setForename("Martin");
        pat.setSurname("Bradley");
        pat.setDob(LocalDate.now());
        pat.setSex(Sex.Male);
        pat.setRowVersion(0);

        new Expectations(){{
            mockRepo.savePatient((Patient)any);
        }};

        long patientId = impl.savePatient(pat, new MessageCollection());
        assertThat(patientId, is(not(-1)));
    }
    @Test
    public void deletePatient_CallsRepo()
    {
        Patient pat= new Patient();
        pat.setForename("Martin");
        pat.setSurname("Bradley");
        pat.setSex(Sex.Male);

        new Expectations(){{
            mockRepo.deletePatient((Patient)any);
        }};

        Object result = impl.deletePatient(pat);
        assertNotNull(result);
    }

    @Test
    public void loadById_calls_resp()
    {
        final Patient repoPatient = new Patient();
        repoPatient.setForename("Martin");
        repoPatient.setSurname("Bradley");
        repoPatient.setDob(LocalDate.now());
        repoPatient.setSex(Sex.Male);
        repoPatient.setPrescription(new ArrayList<Prescription>());

        expectationLoadById(repoPatient);

        Patient result = impl.loadById(1L);

        verifyMockRepoCalledTimes(1);

        assertThat(result, is(not(nullValue())));
        assertThat(result.getForename(), is("Martin"));
    }

    @Test
    public void loadById_not_found()
    {
        expectationLoadById(null);

        final Patient patient = null;

        Patient loadedPatient = impl.loadById(1L);

        verifyMockRepoCalledTimes(1);

        assertThat(loadedPatient, is(nullValue()));
    }

    private void expectationLoadById(Patient patient)
    {
        new Expectations(){{
            mockRepo.loadById(anyLong); result = patient;
        }};
    }

    private void verifyMockRepoCalledTimes(int aTimesCalled)
    {
        new Verifications() {{
            mockRepo.loadById(anyLong); times = aTimesCalled;
        }};
    }
}
