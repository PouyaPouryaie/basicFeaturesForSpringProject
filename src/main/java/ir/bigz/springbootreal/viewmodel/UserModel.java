package ir.bigz.springbootreal.viewmodel;

import javax.persistence.Column;

public class UserModel {

    private long id;
    private String name;
    private int nationalId;

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

    public int getNationalId() {
        return nationalId;
    }

    public void setNationalId(int nationalId) {
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
