package ir.bigz.springbootreal.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name ="users")
@Access(AccessType.FIELD)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements Serializable {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "national_code", nullable = false, unique = true)
    private String nationalCode;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "gender")
    private String gender;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return nationalCode.equals(user.nationalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nationalCode);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", nationalCode='" + nationalCode + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", " + super.toString() +
                "} ";
    }

    public static void updateUserFields(User sourceUser, User updateUser) {
        if (!sourceUser.getFirstName().equals(updateUser.getFirstName())) {
            sourceUser.setFirstName(updateUser.getFirstName());
        }
        if (!sourceUser.getLastName().equals(updateUser.getLastName())) {
            sourceUser.setLastName(updateUser.getLastName());
        }
        if (!sourceUser.getUserName().equals(updateUser.getUserName())) {
            sourceUser.setUserName(updateUser.getUserName());
        }
        if (!sourceUser.getNationalCode().equals(updateUser.getNationalCode())) {
            sourceUser.setNationalCode(updateUser.getNationalCode());
        }
        if (!sourceUser.getEmail().equals(updateUser.getEmail())) {
            sourceUser.setEmail(updateUser.getEmail());
        }
        if (!sourceUser.getMobile().equals(updateUser.getMobile())) {
            sourceUser.setMobile(updateUser.getMobile());
        }
        if (!sourceUser.getGender().equals(updateUser.getGender())) {
            sourceUser.setGender(updateUser.getGender());
        }

        if (!sourceUser.isActiveStatus() == (updateUser.isActiveStatus())) {
            sourceUser.setActiveStatus(updateUser.isActiveStatus());
        }
    }
}
