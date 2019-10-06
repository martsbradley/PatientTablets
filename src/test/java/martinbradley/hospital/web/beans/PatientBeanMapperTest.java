package martinbradley.hospital.web.beans;

import martinbradley.hospital.core.beans.PatientBean;
import martinbradley.hospital.mappers.PatientBeanMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import martinbradley.hospital.core.domain.Patient;
import org.mapstruct.factory.Mappers;
import martinbradley.hospital.core.domain.Sex;
import java.time.LocalDate;

public class PatientBeanMapperTest
{
    @Test
    public void dto_maps_to_bean()
    {
        final PatientBeanMapper mapper = Mappers.getMapper(PatientBeanMapper.class);
        Patient patient = new Patient();

        LocalDate myDate = LocalDate.now();
        patient.setForename("martin");
        patient.setSurname("bradley");
        patient.setSex(Sex.Male);
        patient.setDob(myDate);

        PatientBean patBean = mapper.domainToBean(patient);

        assertEquals("martin",  patBean.getForename());
        assertEquals("bradley", patBean.getSurname());
        assertTrue(patBean.isMale());
        assertEquals(myDate,    patBean.getDob());
    }

    @Test
    public void bean_Maps_to_Domain()
    {
        final PatientBeanMapper mapper = Mappers.getMapper(PatientBeanMapper.class);
        PatientBean patBean = new PatientBean();

        LocalDate myDate = LocalDate.now();
        patBean.setForename("martin");
        patBean.setSurname("bradley");
        patBean.setMale(false);
        patBean.setDob(myDate);

        Patient patient = mapper.beanToDomain(patBean);

        assertEquals("martin",   patient.getForename());
        assertEquals("bradley",  patient.getSurname());
        assertEquals(Sex.Female, patient.getSex());
        assertEquals(myDate,     patient.getDob());
    }
}
