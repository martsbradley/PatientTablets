package martinbradley.hospital.core.domain.password;

import com.sun.mail.auth.OAuth2SaslClient;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


@NamedQuery(name="User.queryUserSalt",
            query="select user.salt from AuthUser user where username = :username")
@Entity
@Table(name="auth_user")
@NamedEntityGraph(name = "graph.AuthUser.prescriptions",
        attributeNodes = {@NamedAttributeNode("authUserGroups")})
public class AuthUser
{
    @Id 
    @SequenceGenerator(name="auth_user_pk_sequence",sequenceName="auth_user_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="auth_user_pk_sequence")
    @Column(name="id")
    private Long id;

    @Column
    private String username;

    @Column
    private String salt;
    @Column
    private String passwordHash;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "user",  cascade=CascadeType.ALL)
    private Set<AuthUserGroup> authUserGroups = Collections.emptySet();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Set<AuthUserGroup> getAuthUserGroups() {
        return authUserGroups;
    }

    public void setAuthUserGroups(Set<AuthUserGroup> groups) {
        this.authUserGroups= authUserGroups;
    }
}
