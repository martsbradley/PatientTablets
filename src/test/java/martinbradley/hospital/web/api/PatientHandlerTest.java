package martinbradley.hospital.web.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.core.domain.Sex;
import martinbradley.hospital.core.beans.MedicineBean;
import martinbradley.hospital.core.beans.PrescriptionBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import mockit.*;
import martinbradley.hospital.core.beans.PatientBean;
import martinbradley.hospital.core.api.PatientBrokerImpl;
import martinbradley.hospital.core.api.dto.MessageCollection;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientHandlerTest
{
    @Mocked PatientBrokerImpl patientBroker;
    PatientHandler impl;

    @BeforeEach
    public void setMeUp()
    {
        impl = new PatientHandler();
        impl.patientBroker = patientBroker;
    }

    @Test
    public void savePatient_calls_PatientBroker()
    {
        PatientBean pat = new PatientBean();
        pat.setForename("Martin");
        pat.setSurname("Bradley");
        pat.setMale(false);
        List<PrescriptionBean> list  = new ArrayList<>();
        PrescriptionBean prescriptionBean  = new PrescriptionBean();
        prescriptionBean.setMedicine(new MedicineBean());
        prescriptionBean.setPatient(pat);
        prescriptionBean.setAmount("one");
        prescriptionBean.setStartDate(LocalDate.now());
        prescriptionBean.setEndDate(LocalDate.now());

        list.add(prescriptionBean);
        pat.setPrescription(list);

        new Expectations(){{
            patientBroker.savePatient((Patient)any, (MessageCollection)any);
        }};

        MessageCollection messages = new MessageCollection();
        impl.savePatient(pat, messages);
    }
    @Test
    public void deletePatient_calls_PatientBroker()
    {
        PatientBean pat = new PatientBean();

        new Expectations(){{
            patientBroker.deletePatient((Patient)any);
        }};

        impl.deletePatient(pat);
    }

    @Test
    public void loadByIdCallsRepository()
    {
        final Patient patient = new Patient();
        patient.setForename("Martin");
        patient.setSurname("Bradley");
        patient.setSex(Sex.Male);
        new Expectations(){{
            patientBroker.loadById(anyLong); result = patient; 
        }};

        impl.loadById(1L);

        new Verifications(){{
            patientBroker.loadById(anyLong); times = 1;
        }};
    }
}
