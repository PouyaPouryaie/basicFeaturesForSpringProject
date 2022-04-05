package ir.bigz.springbootreal.viewmodel;
import ir.bigz.springbootreal.validation.ValidationType;
import ir.bigz.springbootreal.validation.annotation.Validator;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class UserModel extends BaseModel implements Serializable {

    @NotBlank(message = "firstName must not blank")
    private String firstName;

    @NotBlank(message = "lastName must not blank")
    private String lastName;

    @NotBlank(message = "userName must not blank")
    private String userName;

    @NotBlank(message = "national code must not blank")
    @Validator(value = ValidationType.NATIONAL_CODE)
    private String nationalCode;

    @Validator(value = ValidationType.MOBILE, nullOption = true)
    private String mobile;

    @Validator(value = ValidationType.EMAIL, nullOption = true)
    private String email;

    @NotBlank(message = "gender must not blank")
    @Validator(value = ValidationType.GENDER)
    private String gender;

    public UserModel() {
    }

    public UserModel(Long id,
                     Integer version,
                     String insertDate,
                     String updateDate,
                     boolean activeStatus,
                     @NotBlank(message = "firstName must not blank") String firstName,
                     @NotBlank(message = "lastName must not blank") String lastName,
                     @NotBlank(message = "userName must not blank") String userName,
                     @Validator(ValidationType.NATIONAL_CODE) String nationalCode,
                     @Validator(value = ValidationType.MOBILE, nullOption = true) String mobile,
                     @Validator(value = ValidationType.EMAIL, nullOption = true) String email,
                     @Validator(ValidationType.GENDER) String gender) {
        super(id, version, insertDate, updateDate, activeStatus);
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.nationalCode = nationalCode;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
    }

    public UserModel(@NotBlank(message = "firstName must not blank") String firstName,
                     @NotBlank(message = "lastName must not blank") String lastName,
                     @NotBlank(message = "userName must not blank") String userName,
                     @Validator(ValidationType.NATIONAL_CODE) String nationalCode,
                     @Validator(ValidationType.MOBILE) String mobile,
                     @Validator(ValidationType.EMAIL) String email,
                     @Validator(ValidationType.GENDER) String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.nationalCode = nationalCode;
        this.mobile = mobile;
        this.email = email;
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", nationalCode='" + nationalCode + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                '}';
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
