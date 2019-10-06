package martinbradley.hospital.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Order;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import javax.persistence.NamedQuery;

@Entity
@Table(name="medicine")
@NamedQuery(name="Medicine.similarNameQuery",
            query="select med from Medicine med where name like :querystring")
public class Medicine
{
    @Id 
    @SequenceGenerator(name="med_pk_sequence",sequenceName="medicine_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="med_pk_sequence")
    @Column(name="id")
    private long id;

    @Column
    private String name;

    @Column
    private String manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(name="delivery_method")
    private MedicineType deliveryMethod;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        this.manufacturer = manufacturer;
    }

    public MedicineType getDeliveryMethod()
    {
        return deliveryMethod;
    }

    public void setDeliveryMethod(MedicineType deliveryMethod)
    {
        this.deliveryMethod = deliveryMethod;
    }

    public static enum SortOrder
    {
        ID_ASC              ("id", true),
        NAME_ASC            ("name", true),
        MANUFACTURER_ASC    ("manufacturer",true),
        DELIVERY_METHOD_ASC ("deliveryMethod",true),
        ID_DESC             ("id", false),
        NAME_DESC           ("name", false),
        MANUFACTURER_DESC   ("manufacturer",false),
        DELIVERY_METHOD_DESC("deliveryMethod",false);

        private SortOrder(String name, boolean ascending)
        {
            this.name = name;
            this.ascending = ascending;
        }
        private final String name;
        private final boolean ascending;

        public Order getOrder(CriteriaBuilder builder, Root<Medicine> root)
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
}
