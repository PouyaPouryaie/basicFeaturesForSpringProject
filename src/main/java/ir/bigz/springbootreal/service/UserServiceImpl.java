package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.commons.util.Utils;
import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.dto.SqlOperation;
import ir.bigz.springbootreal.dto.ValueCondition;
import ir.bigz.springbootreal.dto.entity.User;
import ir.bigz.springbootreal.dto.entity.User_;
import ir.bigz.springbootreal.dto.mapper.UserMapper;
import ir.bigz.springbootreal.exception.AppException;
import ir.bigz.springbootreal.exception.HttpErrorCode;
import ir.bigz.springbootreal.viewmodel.UserModel;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.javatuples.Quartet;
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
    private static final String USER_QUERY = "select * from users";


    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    @Cacheable(value = "userCache", key = "#userId", condition = "#userId != null", unless = "#result==null")
    public UserModel getUser(Long userId) {
        try {
            Optional<User> user = userRepository.find(userId);
            return userMapper.userToUserModel(user.get());
        } catch (RuntimeException exception) {
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
            if (userRepository.getUserWithNationalCode(userModel.getNationalCode()) == null) {
                userModel.setInsertDate(Utils.getLocalTimeNow());
                userModel.setActiveStatus(true);
                User user = userMapper.userModelToUser(userModel);
                User insert = userRepository.insert(user);
                return userMapper.userToUserModel(insert);
            }
            throw new RuntimeException("user has already exist");

        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10700, String.format("user has already existed with %s nationalId", userModel.getNationalCode())
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CachePut(value = "userCache", key = "#userId", condition = "#userId != null", unless = "#result==null")
    public UserModel updateUser(long userId, UserModel userModel) {
        try {
            Optional<User> user = userRepository.find(userId);
            User sourceUser = user.get();
            User updateUser = userMapper.userModelToUser(userModel);
            mapUserForUpdate(sourceUser, updateUser);
            sourceUser.setUpdateDate(Utils.getTimestampNow());
            return userMapper.userToUserModel(sourceUser);
        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10703,
                    String.format("user with userId %s, not found", userId)
            );
        }
    }


    @Override
    @CacheEvict(value = "userCache", beforeInvocation = true, key = "#userId")
    public String deleteUser(long userId) {
        try {
            userRepository.find(userId);
            userRepository.delete(userId);
            return "Success";

        } catch (RuntimeException exception) {
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
        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Page<UserModel> getUserSearchResult(UserSearchDto userSearchDto, String sortOrder, Sort.Direction direction, Integer pageNumber, Integer pageSize) {
        try {
            Sort.Order order = new Sort.Order(direction, sortOrder);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order));
            Page<User> users = userRepository.getUserSearchResult(userSearchDto, order, pageable);
            List<UserModel> collect = users.get().map(userMapper::userToUserModel).collect(Collectors.toList());
            return new PageImpl<>(collect, pageable, users.getTotalElements());
        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Page<UserModel> getAllUserPage(String sortOrder, Sort.Direction direction, Integer pageNumber, Integer pageSize) {
        Sort.Order order = new Sort.Order(direction, sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order));
        Page<User> all = userRepository.getAll(pageable);
        List<UserModel> collect = all.get().map(userMapper::userToUserModel).collect(Collectors.toList());
        return new PageImpl<>(collect, pageable, all.getTotalElements());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public PageResult<UserModel> getUserSearchWithNativeQuery(Map<String, String> queryString, PagedQuery pagedQuery) {

        Map<String, Object> parametersMap = new HashMap<>();
        Map<String, String> conditionsMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(USER_QUERY); //base query
        stringBuilder.append(Utils.getWhereSimple()); //add where to query
        Map<String, String> parameterQueryWithFieldNameMap = new HashMap<>(); //map between queryString and entityColumn
        parameterQueryWithFieldNameMap.put("firstName", "first_name");
        parameterQueryWithFieldNameMap.put("lastName", "last_name");
        Map<String, SqlOperation> parameterQueryWithOperationMap = new HashMap<>(); // map between queryString and queryCondition
        parameterQueryWithOperationMap.put("firstName", SqlOperation.CONTAINS);
        parameterQueryWithOperationMap.put("lastName", SqlOperation.CONTAINS);

        Utils.buildNativeQueryCondition(queryString,
                conditionsMap,
                parametersMap,
                parameterQueryWithFieldNameMap,
                parameterQueryWithOperationMap,
                String.class);

        conditionsMap.keySet().forEach(s -> stringBuilder.append(conditionsMap.get(s)));

        PageResult<User> userPageResult = userRepository.pageCreateQuery(stringBuilder.toString(), pagedQuery, parametersMap, true);

        List<UserModel> collect = userPageResult.getResult().stream().map(userMapper::userToUserModel).collect(Collectors.toList());

        return new PageResult<>(collect,
                userPageResult.getPageSize(),
                userPageResult.getPageNumber(),
                userPageResult.getOffset(),
                userPageResult.getTotal());
    }

    @Override
    public Page<UserModel> getUserSearchWithCriteriaBuilder(Map<String, String> queryString, PagedQuery pagedQuery) {

        // tuple define for map data from queryString in query search base on sql operation and value condition rules
        List<Quartet<String, String, SqlOperation, ValueCondition>> rules = new ArrayList<>();
        Quartet<String, String, SqlOperation, ValueCondition> firstNameTuple = new Quartet<>(
                "firstName", "firstName", SqlOperation.CONTAINS, ValueCondition.CONTAINS);
        Quartet<String, String, SqlOperation, ValueCondition> insertDateTuple = new Quartet<>(
                "dateFrom", "insertDate", SqlOperation.GREATER_THAN, ValueCondition.EQUAL);
        rules.add(firstNameTuple);
        rules.add(insertDateTuple);

        // define pageable object base on pagedQuery
        List<Sort.Order> orderFromPagedQuery = Utils.getSortOrderFromPagedQuery(pagedQuery, User.class);
        Pageable pageable = PageRequest.of(pagedQuery.getPageNumber(),
                pagedQuery.getPageSize(),
                Sort.by(orderFromPagedQuery));

        // call repo
        // tuples, queryString, pageable to repository
        try{
            Page<User> userQueryWithCriteriaBuilder = userRepository.getUserQueryWithCriteriaBuilder(queryString, rules, pageable);
            List<UserModel> collect = userQueryWithCriteriaBuilder.get().map(userMapper::userToUserModel).collect(Collectors.toList());
            return new PageImpl<>(collect, pageable, userQueryWithCriteriaBuilder.getTotalElements());
        }catch (RuntimeException exception) {
            throw AppException.newInstance(
                    HttpErrorCode.ERR_10701,
                    String.format("getUserSearchWithCriteriaBuilder method has error: %s", exception.getCause())
            );
        }
    }

    private void mapUserForUpdate(User sourceUser, User updateUser) {
        if (!sourceUser.getFirstName().equals(updateUser.getFirstName())) {
            sourceUser.setFirstName(updateUser.getFirstName());
        }
        if (!sourceUser.getLastName().equals(updateUser.getLastName())) {
            sourceUser.setLastName(updateUser.getLastName());
        }
        if (!sourceUser.getUserName().equals(updateUser.getUserName())) {
            sourceUser.setUserName(updateUser.getUserName());
        }
        if (!sourceUser.getNationalCode().equals(updateUser.getNationalCode())) {
            sourceUser.setNationalCode(updateUser.getNationalCode());
        }
        if (!sourceUser.getEmail().equals(updateUser.getEmail())) {
            sourceUser.setEmail(updateUser.getEmail());
        }
        if (!sourceUser.getMobile().equals(updateUser.getMobile())) {
            sourceUser.setMobile(updateUser.getMobile());
        }
        if (!sourceUser.getGender().equals(updateUser.getGender())) {
            sourceUser.setGender(updateUser.getGender());
        }

        if (!sourceUser.isActiveStatus() == (updateUser.isActiveStatus())) {
            sourceUser.setActiveStatus(updateUser.isActiveStatus());
        }
    }
}
