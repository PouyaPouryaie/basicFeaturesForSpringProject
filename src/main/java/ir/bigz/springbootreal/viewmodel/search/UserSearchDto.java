package ir.bigz.springbootreal.viewmodel.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

//@NoArgsConstructor
//@Getter
//@Setter
//@ToString
@Data
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
