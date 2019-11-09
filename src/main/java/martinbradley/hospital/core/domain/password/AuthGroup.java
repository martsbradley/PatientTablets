package martinbradley.hospital.core.domain.password;

import javax.persistence.*;

@Entity
@Table(name="auth_group")
public class AuthGroup
{
    @Id 
    @SequenceGenerator(name="auth_group_pk_sequence",sequenceName="auth_group_id_seq", allocationSize=1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE,generator="auth_group_pk_sequence")
    @Column(name="id")
    private long id;

    @Column
    private String name;

    @Column
    private String description;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AuthGroup[" + name + "]";
    }
}
