package ir.bigz.springbootreal.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer version;

    @Column(name = "insert_date")
    private Timestamp insertDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @Column(name = "active_status")
    public boolean activeStatus;

    static BaseEntity baseEntityCreateFactory(){
        BaseEntity baseEntity = new BaseEntity();
        baseEntity.insertDate = Timestamp.valueOf(LocalDateTime.now());
        return baseEntity;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", version=" + version +
                ", insertDate=" + insertDate +
                ", updateDate=" + updateDate +
                ", activeStatus=" + activeStatus;
    }
}
