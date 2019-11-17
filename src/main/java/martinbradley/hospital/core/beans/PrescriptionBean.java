package martinbradley.hospital.core.beans;
import java.time.LocalDate;

import martinbradley.hospital.core.beans.MedicineBean;
import martinbradley.hospital.core.beans.PatientBean;

import java.io.Serializable;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import martinbradley.hospital.jaxb.LocalDateAdapter;

@XmlRootElement(name="prescription")
@XmlAccessorType(XmlAccessType.FIELD)
public class PrescriptionBean implements Serializable
{
    @XmlElement
    private Long id;

    @NotNull
    @XmlTransient
    private PatientBean patient;

    @XmlElement
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    @NotNull
    private LocalDate startDate, endDate;

    @NotNull
    @XmlElement
    private String amount;

    @NotNull
    @XmlElement
    private MedicineBean medicine;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public PatientBean getPatient()
    {
        return patient;
    }
    public void setPatient(PatientBean patient)
    {
        this.patient = patient;
    }

    public LocalDate getStartDate()
    {
        return startDate;
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
    }

    public LocalDate getEndDate()
    {
        return endDate;
    }

    public void setEndDate(LocalDate endDate)
    {
        this.endDate = endDate;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public void setMedicine(MedicineBean medicine)
    {
        this.medicine = medicine;
    }

    public MedicineBean getMedicine()
    {
        return this.medicine;
    }
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("PrescriptionBean [");
        sb.append(" Id:");
        sb.append(this.id);
        sb.append(" Start Date:");
        sb.append(this.startDate);
        sb.append(" Amount:");
        sb.append(this.amount);
        sb.append(" ,");
        sb.append(patient == null? " patient is null ": patient.getForename());
        sb.append(" ,");
        sb.append(medicine == null? " med is null ": medicine.getName());
        sb.append("]");
        return sb.toString();
    }
}
