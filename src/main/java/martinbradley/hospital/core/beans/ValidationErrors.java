package martinbradley.hospital.core.beans;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name="ValidationErrors")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationErrors implements Serializable
{
    @XmlElement
    private List<ValidationError> errors;


    public ValidationErrors()
    {
	errors = new ArrayList<>();
    }


    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
	if (errors == null) throw new IllegalArgumentException();
        this.errors = errors;
    }

    public void add(ValidationError error) {
	errors.add(error);
    }

    public boolean hasErrors() {
	return !errors.isEmpty();
    }

}
