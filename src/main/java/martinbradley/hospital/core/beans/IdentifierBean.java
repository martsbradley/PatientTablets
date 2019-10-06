package martinbradley.hospital.core.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import java.io.Serializable;

@XmlRootElement(name="identifier")
@XmlAccessorType(XmlAccessType.FIELD)
public class IdentifierBean implements Serializable
{
    @XmlElement
    private Long id;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
