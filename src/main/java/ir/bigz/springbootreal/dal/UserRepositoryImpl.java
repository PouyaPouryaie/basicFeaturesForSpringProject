package ir.bigz.springbootreal.dal;

import ir.bigz.springbootreal.dao.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryImpl extends DaoRepositoryImpl<User, Long> implements UserRepository {

    @Override
    public User getUserWithNationalId(String nationalId) {

        List<User> resultList = new ArrayList<>();
        String query = "select u from User u where u.nationalId like '" + nationalId + "'";

        resultList = genericSearch(query);

        if(resultList.size() > 0){
            return resultList.get(0);
        }
        return null;
    }
}
