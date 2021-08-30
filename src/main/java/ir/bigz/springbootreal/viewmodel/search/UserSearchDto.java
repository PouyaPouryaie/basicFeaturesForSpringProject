package ir.bigz.springbootreal.viewmodel.search;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserSearchDto implements Serializable {

    private String firstName;

    private String lastName;

    private String userName;

    private String nationalCode;

    private String mobile;

    private String email;

    private String gender;

}
