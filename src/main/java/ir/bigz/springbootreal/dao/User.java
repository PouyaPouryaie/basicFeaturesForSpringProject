package ir.bigz.springbootreal.dao;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name ="users")
@Access(AccessType.FIELD)
public class User implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "national_id", nullable = false, unique = true)
    private String nationalId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nationalId.equals(user.nationalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nationalId);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nationalId='" + nationalId + '\'' +
                '}';
    }
}
