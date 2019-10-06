package martinbradley.hospital.mappers;
import martinbradley.hospital.core.beans.PrescriptionBean;
import martinbradley.hospital.core.domain.Patient;
import martinbradley.hospital.core.domain.Prescription;

import martinbradley.hospital.core.beans.PatientBean;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;

@Mapper(uses= SexToBoolean.class)
public interface PatientBeanMapper
{
    @Mapping(target="male",  source="sex",
             qualifiedBy = { SexTranslator.class})
    PatientBean domainToBean(Patient patient);

    @Mapping(target="sex", source="male",
             qualifiedBy = { SexTranslator.class})
    Patient beanToDomain(PatientBean bean);

    /** There is a circular reference
     *  from patient to prescription but also 
     *  from prescription.patient back to the same patient
     *
     *  To handle this the patient field is ignored when mapping the
     *  prescription, but then the AfterMapping of the Patient/PatientBean
     *  updates the references.
     */

    @Mapping( target = "patient", ignore = true )
    Prescription prescriptionBeanToDomain(PrescriptionBean bean);

    @Mapping( target = "patient", ignore = true )
    PrescriptionBean prescriptionDomainToBean(Prescription prescription);

    @AfterMapping
    default void addPatientBeanBackReference(@MappingTarget PatientBean target) 
    {
        for (PrescriptionBean child : target.getPrescription() ) 
        {
            child.setPatient(target);
        }
    }

    @AfterMapping
    default void addPatientBackReference(@MappingTarget Patient target) 
    {
        for (Prescription child : target.getPrescription() ) 
        {
            child.setPatient(target);
        }
    }
}
