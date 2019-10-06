package martinbradley.hospital.core.domain;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.Column;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.NamedQuery;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedAttributeNode;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Order;
import java.time.LocalDate;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name="patient")
@NamedQuery (name="Patient.withSameName", 
             query="SELECT c FROM Patient c where forename =:forename and surname =:surname") 
@NamedEntityGraph(name = "graph.Patient.prescriptions", 
                  attributeNodes = {@NamedAttributeNode("prescription")})
public class Patient 
{
    @Id 
    @SequenceGenerator(name="patient_pk_sequence",sequenceName="patient_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="patient_pk_sequence")
    @Column(name="id")
    private Long id;

    private String forename;

    private String surname;

    @Enumerated(EnumType.STRING)
    @Column(name="sex")
    private Sex sex;
    
    @OneToMany(fetch=FetchType.LAZY, mappedBy = "patient",  cascade=CascadeType.ALL)
    private List<Prescription> prescription = new ArrayList<>();

    @Column(name="dateofbirth")
    private LocalDate dob;

    @Column(name="version")
    @Version
    private Integer rowVersion;

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
    public Sex getSex()
    {
        return sex;
    }
    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public List<Prescription> getPrescription()
    {
      //if (prescription == null)
      //{
      //    prescription = new ArrayList<>();
      //}
        return prescription;
    }
    public void setPrescription(List<Prescription> prescription)
    {
        this.prescription = prescription;
    }
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient [");
        sb.append(id);
        sb.append(",");
        sb.append(forename);
        sb.append(",");
        sb.append(surname);
        sb.append(",");
        sb.append(dob);
        sb.append(" version");
        sb.append(rowVersion);
        sb.append("]");

        sb.append("Prescriptions\n[\n");
        if (prescription != null)
        {
            for (Prescription p : prescription)
            {
                sb.append("\n");
                sb.append(p);
            }
        }
        sb.append("\n]\n");
        return sb.toString();
    }

    public static enum SortOrder
    {
        ID_ASC        ("id",       true),
        FORENAME_ASC  ("forename", true),
        SURNAME_ASC   ("surname",  true),
        SEX_ASC       ("sex",      true),
        ID_DESC       ("id",       false),
        FORENAME_DESC ("forename", false),
        SURNAME_DESC  ("surname",  false),
        SEX_DESC      ("sex",      false);

        private SortOrder(String name, boolean ascending)
        {
            this.name = name;
            this.ascending = ascending;
        }
        private final String name;
        private final boolean ascending;

        public Order getOrder(CriteriaBuilder builder, Root<Patient> root)
        {
            if (ascending)
            {
                return builder.asc(root.get(name));
            }
            else
            {
                return builder.desc(root.get(name));
            }
        }

        public static SortOrder find(String name, boolean isAscending)
        {
            if (StringUtils.isEmpty(name)) return ID_ASC;

            for (SortOrder o : values())
            {
                if (name.equalsIgnoreCase(o.name) &&
                        isAscending == o.ascending)
                {
                    return o;
                }
            }
           throw new RuntimeException("SortOrder::find null for '" + name + "'");
        }
    }
    public void setRowVersion(Integer rowVersion) {
        this.rowVersion = rowVersion;
    }
    public Integer getRowVersion() {
        return this.rowVersion;
    }
}
