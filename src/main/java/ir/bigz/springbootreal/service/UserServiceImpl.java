package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserServiceImpl implements UserService {

    final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    //@Autowired
    //UserRepository userRepository;

    final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    @Cacheable(value = "userCache", key="#userId", condition = "#userId != null", unless = "#result==null")
    public User getUser(long userId) {

        return userRepository.find(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User addUser(UserModel userModel) {
        User user = new User();
        if(userRepository.getUserWithNationalId(userModel.getNationalId()) == null){
            user.setName(userModel.getName());
            user.setNationalId(userModel.getNationalId());
            return userRepository.insert(user);
        }
        else{
            LOG.info("user has already existed not created");
            return null;
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CacheEvict(value = "userCache", key = "#userId")
    public String deleteUser(long userId){

        try{
            if(getUser(userId) != null) {
                userRepository.delete(userId);
                return "Success";
            }
            else{
                LOG.info("user not found");
                return "failed";
            }
        }catch (Exception e){
            LOG.error("delete user not complete \n" + e.getMessage());
            return "failed";
        }
    }
}
