package martinbradley.hospital.core.beans;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.XmlID;
import martinbradley.hospital.jaxb.LocalDateAdapter;

@Named
@XmlRootElement(name="UserPassword")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserPassword implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Size(min=8, max=20)
    @XmlElement
    private String username;

    @Size(min=8)
    @XmlElement
    private String password;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "UserPassword [" + username + "]";
    }
}
