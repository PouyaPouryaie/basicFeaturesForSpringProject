package ir.bigz.springbootreal.viewmodel;

import lombok.Value;

@Value
public class UserModelResponse {

    String firstName;
    String lastName;
    String userName;
    String nationalCode;
    String mobile;
    String email;
    String gender;
}
