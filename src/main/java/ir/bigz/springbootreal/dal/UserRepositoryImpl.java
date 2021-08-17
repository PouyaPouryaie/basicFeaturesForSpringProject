package ir.bigz.springbootreal.dal;

import ir.bigz.springbootreal.dao.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRepositoryImpl extends DaoRepositoryImpl<User, Long> implements UserRepository {

    final Logger LOG = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Override
    public User getUserWithNationalId(String nationalId) {

        User u = null;
        List<User> resultList = new ArrayList<>();
        String query = "select u from User u where u.nationalId like '" + nationalId + "'";

        try {
            resultList = genericSearch(query);
            if(resultList.get(0)!= null){
                u = resultList.get(0);
            }
        }catch (Exception e){
            LOG.info("user not found message \n" + e.getMessage());
        }
        return u;
    }
}
