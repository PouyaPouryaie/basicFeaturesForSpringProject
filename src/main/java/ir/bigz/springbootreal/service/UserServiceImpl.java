package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.dao.mapper.UserMapper;
import ir.bigz.springbootreal.exception.AppException;
import ir.bigz.springbootreal.exception.HttpErrorCode;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserServiceImpl implements UserService {

    final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    @Cacheable(value = "userCache", key="#userId", condition = "#userId != null", unless = "#result==null")
    public UserModel getUser(Long userId) {
        try {
            Optional<User> user = userRepository.find(userId);
            return userMapper.userToUserModel(user.get());
        }catch (RuntimeException exception){
            LOG.info("user not found");
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10702,
                    String.format("not found user with id : %s", userId)
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserModel addUser(UserModel userModel) {
        if(userRepository.getUserWithNationalId(userModel.getNationalId()) == null){
            User user = userMapper.userModelToUser(userModel);
            return userMapper.userToUserModel(userRepository.insert(user));
        }
        else{
            LOG.info("user has already existed not created");
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10700,
                    String.format("user existed with %s nationalId", userModel.getNationalId())
            );
        }
    }


    @Override
    @CacheEvict(value = "userCache", beforeInvocation = true, key = "#userId")
    public String deleteUser(long userId){

        try{
            if(getUser(userId) != null) {
                userRepository.delete(userId);
                return "Success";
            }
            else{
                LOG.info("user not found");
                return "user not found";
            }
        }catch (Exception e){
            LOG.error("delete user not complete \n" + e.getMessage());
            return "failed";
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public List<UserModel> getAll() {
        try {
            Stream<User> allUser = userRepository.getAll();
            return allUser.map(userMapper::userToUserModel).collect(Collectors.toList());
        }catch (RuntimeException exception){
            LOG.info("getAll method has error \n" + exception.getMessage());
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }
}
