package martinbradley.hospital.core.beans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlID;

@XmlRootElement(name="ValidationError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationError implements Serializable
{
    @XmlElement
    private String field;

    @XmlElement
    private String message;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
