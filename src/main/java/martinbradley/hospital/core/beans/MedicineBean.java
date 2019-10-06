package martinbradley.hospital.core.beans;
import javax.validation.constraints.*;

public class MedicineBean
{
    private long id;
    @Size(min=1, max=15)
    private String name;

    @Size(min=1, max=20)
    private String manufacturer;

    @NotNull
    private String deliveryMethod;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }
}
