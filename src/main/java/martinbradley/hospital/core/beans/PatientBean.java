package martinbradley.hospital.core.beans;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import javax.validation.constraints.*;
import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import martinbradley.hospital.jaxb.LocalDateAdapter;

@XmlRootElement(name="patient")
@XmlAccessorType(XmlAccessType.FIELD)
public class PatientBean implements Serializable
{
    private static final long serialVersionUID = 1L;

    @XmlElement
    private Long id;

    @Size(min=1, max=20)
    @XmlElement
    private String surname;

    @Size(min=1, max=15)
    @XmlElement
    private String forename;

    @XmlElement
    private boolean male;

    @NotNull
    @XmlElement
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    private LocalDate dob = LocalDate.now();

    @NotNull
    @XmlElement
    private Integer rowVersion;

    @XmlElement
    private List<PrescriptionBean> prescription = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(PatientBean.class);

    public PatientBean()
    {
        //logger.debug("PatientBean was created");
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getForename() {
        return forename;
    }
    public void setForename(String forename) {
        this.forename = forename;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public LocalDate getDob() {
        return dob;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public boolean isMale()
    {
        return male;
    }
    public void setMale(boolean male)
    {
        this.male = male;
    }

    public List<PrescriptionBean> getPrescription()
    {
        return prescription;
    }

    public void setPrescription(List<PrescriptionBean> prescription)
    {
        this.prescription = prescription;
    }

    public void addPrescription(PrescriptionBean aPrescription)
    {
        this.prescription.add(aPrescription);
    }

    @Override
    public String toString()
    {
        String version = this.rowVersion == null? "null": this.rowVersion.toString();

        return "Patient [" + id + ", " 
                           + forename + " " 
                           + surname +   " " 
                           + dob + " " 
                           + version + "]";
    }
    public String addPatient(PatientBean patient)
    {
	logger.info("addPatient(" + patient + ")");
        return "greeting";
    }

    public void setRowVersion(Integer rowVersion) {
        this.rowVersion = rowVersion;
    }
    public Integer getRowVersion() {
        return this.rowVersion;
    }
}
