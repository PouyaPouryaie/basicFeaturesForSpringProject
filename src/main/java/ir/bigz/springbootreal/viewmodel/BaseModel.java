package ir.bigz.springbootreal.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel {
    private Long id;
    private Integer version;
    private String insertDate;
    private String updateDate;
    public boolean activeStatus;
}
