package ir.bigz.springbootreal.viewmodel.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSearchDto implements Serializable {

    private String firstName;

    private String lastName;

    private String userName;

    private String nationalCode;

    private String mobile;

    private String email;

    private String gender;

}
