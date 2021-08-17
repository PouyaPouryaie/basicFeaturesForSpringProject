package ir.bigz.springbootreal.viewmodel;
import java.io.Serializable;

public class UserModel implements Serializable {

    private long id;
    private String name;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nationalId=" + nationalId +
                '}';
    }
}
