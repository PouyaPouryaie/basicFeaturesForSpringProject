package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.dao.mapper.UserMapper;
import ir.bigz.springbootreal.exception.AppException;
import ir.bigz.springbootreal.exception.HttpErrorCode;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserServiceImpl implements UserService {

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
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10702,
                    String.format("user with id, %s not found", userId)
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserModel addUser(UserModel userModel) {
        try {
            if(userRepository.getUserWithNationalCode(userModel.getNationalCode()) == null){
                User user = userMapper.userModelToUser(userModel);
                return userMapper.userToUserModel(userRepository.insert(user));
            }
            throw new RuntimeException("user has already exist");

        }catch (RuntimeException exception){
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10700, String.format("user has already existed with %s nationalId", userModel.getNationalCode())
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CachePut(value = "userCache", key="#userId", condition = "#userId != null", unless = "#result==null")
    public UserModel updateUser(long userId, UserModel userModel) {
        try {
            Optional<User> user = userRepository.find(userId);
            User sourceUser = user.get();
            User updateUser = userMapper.userModelToUser(userModel);
            mapUserForUpdate(sourceUser, updateUser);
            return userMapper.userToUserModel(sourceUser);
        }catch (RuntimeException exception){
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10703,
                    String.format("user with userId %s, not found", userId)
            );
        }
    }


    @Override
    @CacheEvict(value = "userCache", beforeInvocation = true, key = "#userId")
    public String deleteUser(long userId){
        try{
            userRepository.find(userId);
            userRepository.delete(userId);
            return "Success";

        }catch (RuntimeException exception){
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("user with id %s, not found", userId)
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public List<UserModel> getAll() {
        try {
            Stream<User> allUser = userRepository.getAll();
            return allUser.map(userMapper::userToUserModel).collect(Collectors.toList());
        }catch (RuntimeException exception){
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Page<UserModel> getUserSearchResult(Integer pageNumber, Integer pageSize, String sortOrder) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            String query = "select u from User u order by u.id " + sortOrder;
            Page<User> users = userRepository.genericSearch(query, pageable);
            List<UserModel> collect = users.get().map(userMapper::userToUserModel).collect(Collectors.toList());
            return new PageImpl<>(collect, pageable, users.getTotalElements());
        }catch (RuntimeException exception){
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Page<UserModel> getAllUserPagebale(Integer pageNumber, Integer pageSize, String sortOrder){
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order));
        Page<User> all = userRepository.getAll(pageable);
        List<UserModel> collect = all.get().map(userMapper::userToUserModel).collect(Collectors.toList());
        return new PageImpl<>(collect, pageable, all.getTotalElements());
    }

    private void mapUserForUpdate(User sourceUser, User updateUser){
        if(!sourceUser.getFirstName().equals(updateUser.getFirstName())){
            sourceUser.setFirstName(updateUser.getFirstName());
        }
        if(!sourceUser.getLastName().equals(updateUser.getLastName())){
            sourceUser.setLastName(updateUser.getLastName());
        }
        if(!sourceUser.getUserName().equals(updateUser.getUserName())){
            sourceUser.setUserName(updateUser.getUserName());
        }
        if(!sourceUser.getNationalCode().equals(updateUser.getNationalCode())){
            sourceUser.setNationalCode(updateUser.getNationalCode());
        }
        if(!sourceUser.getEmail().equals(updateUser.getEmail())){
            sourceUser.setEmail(updateUser.getEmail());
        }
        if(!sourceUser.getMobile().equals(updateUser.getMobile())){
            sourceUser.setMobile(updateUser.getMobile());
        }
        if(!sourceUser.getGender().equals(updateUser.getGender())){
            sourceUser.setGender(updateUser.getGender());
        }
    }
}
