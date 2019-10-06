package martinbradley.auth0;

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


/*
 * create table logindetails (
 * id SERIAL PRIMARY KEY,
 * name varchar(255),
 * given_name varchar(255),
 * family_name varchar(255),
 * nickname varchar(50),
 * locale varchar(10),
 * updated_at varchar(50),
 * picture varchar(250)
 * groups varchar(250)
 * );
 */

@Entity
@Table(name="logindetails")
public class LoginDetail 
{
    @Id 
    @SequenceGenerator(name="logindetails_pk_sequence",sequenceName="logindetails_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="logindetails_pk_sequence")
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="given_name")
    private String givenName;

    @Column(name="family_name")
    private String familyName;

    @Column(name="nickname")
    private String nickname;

    @Column(name="locale")
    private String locale;

    @Column(name="updated_at")
    private String updatedAtTime;

    @Column(name="picture")
    private String picture;

    @Column(name="groups")
    private String groups;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGivenName() {
        return givenName;
    }
    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUpdatedAtTime() {
        return updatedAtTime;
    }
    public void setUpdatedAtTime(String updatedAtTime) {
        this.updatedAtTime = updatedAtTime;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient [");
        sb.append(id);
        sb.append(",");
        sb.append(givenName);
        sb.append(",");
        sb.append(familyName);
        sb.append(",");
        sb.append(nickname);
        sb.append(" locale");
        sb.append(locale);
        sb.append(" updatedAtTime");
        sb.append(updatedAtTime);
        sb.append("]\n");

        return sb.toString();
    }
}
