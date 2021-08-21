package ir.bigz.springbootreal.dal;

import ir.bigz.springbootreal.dao.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends DaoRepository<User,Long> {

    User getUserWithNationalCode(String nationalCode);
}
