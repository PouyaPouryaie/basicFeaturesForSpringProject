package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.commons.util.Utils;
import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.dto.SqlOperation;
import ir.bigz.springbootreal.dto.ValueCondition;
import ir.bigz.springbootreal.dto.entity.User;
import ir.bigz.springbootreal.dto.mapper.UserMapper;
import ir.bigz.springbootreal.exception.AppException;
import ir.bigz.springbootreal.exception.SampleExceptionType;
import ir.bigz.springbootreal.viewmodel.UserModelRequest;
import ir.bigz.springbootreal.viewmodel.UserModelResponse;
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
    public UserModelResponse getUser(Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() ->
                            AppException.newInstance(
                                    SampleExceptionType.USER_NOT_FOUND,
                                    String.format("user with id, %s not found", userId)));
            return userMapper.userToUserModelResponse(user);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public UserModelResponse addUser(UserModelRequest userModelRequest) {
        try {
            if (userRepository.getUserWithNationalCode(userModelRequest.getNationalCode()) == null) {
                userModelRequest.setInsertDate(Utils.getLocalTimeNow());
                userModelRequest.setActiveStatus(true);
                User user = userMapper.userModelToUser(userModelRequest);
                User insert = userRepository.save(user);
                return userMapper.userToUserModelResponse(insert);
            }
            throw new RuntimeException("user has already exist");

        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    SampleExceptionType.INVALID_ENTITY_FOR_INSERT, String.format("user has already existed with %s nationalId", userModelRequest.getNationalCode())
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @CachePut(value = "userCache", key = "#userId", condition = "#userId != null", unless = "#result==null")
    public UserModelResponse updateUser(long userId, UserModelRequest userModelRequest) {
        try {
            User sourceUser = userRepository
                    .findById(userId)
                    .orElseThrow(() -> AppException.newInstance(
                            SampleExceptionType.INVALID_ENTITY_FOR_UPDATE,
                            String.format("user with userId %s, not found", userId)));
            User updateUser = userMapper.userModelToUser(userModelRequest);
            User.updateUserFields(sourceUser, updateUser);
            sourceUser.setUpdateDate(Utils.getTimestampNow());
            return userMapper.userToUserModelResponse(sourceUser);
        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    SampleExceptionType.INVALID_ENTITY_FOR_UPDATE,
                    String.format("user with userId %s, not found", userId)
            );
        }
    }


    @Override
    @CacheEvict(value = "userCache", beforeInvocation = true, key = "#userId")
    public String deleteUser(long userId) {
        try {
            userRepository.findById(userId);
            userRepository.deleteById(userId);
            return "Success";

        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    SampleExceptionType.INTERNAL_ERROR,
                    String.format("user with id %s, not found", userId)
            );
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public List<UserModelResponse> getAll() {
        try {
            Stream<User> allUser = userRepository.findAll();
            return allUser.map(userMapper::userToUserModelResponse).collect(Collectors.toList());
        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    SampleExceptionType.INTERNAL_ERROR,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Page<UserModelResponse> getUserSearchResult(UserSearchDto userSearchDto, String sortOrder, Sort.Direction direction, Integer pageNumber, Integer pageSize) {
        try {
            Sort.Order order = new Sort.Order(direction, sortOrder);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order));
            Page<User> users = userRepository.getUserSearchResult(userSearchDto, order, pageable);
            List<UserModelResponse> collect = users.get().map(userMapper::userToUserModelResponse).collect(Collectors.toList());
            return new PageImpl<>(collect, pageable, users.getTotalElements());
        } catch (RuntimeException exception) {
            throw AppException.newInstance(
                    SampleExceptionType.INTERNAL_ERROR,
                    String.format("getAll method has error: %s", exception.getCause())
            );
        }

    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public Page<UserModelResponse> getAllUserPage(String sortOrder, Sort.Direction direction, Integer pageNumber, Integer pageSize) {
        Sort.Order order = new Sort.Order(direction, sortOrder);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order));
        Page<User> all = userRepository.findAll(pageable);
        List<UserModelResponse> collect = all.get().map(userMapper::userToUserModelResponse).collect(Collectors.toList());
        return new PageImpl<>(collect, pageable, all.getTotalElements());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT)
    public PageResult<UserModelResponse> getUserSearchWithNativeQuery(Map<String, String> queryString, PagedQuery pagedQuery) {

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

        List<UserModelResponse> collect = userPageResult.getResult().stream().map(userMapper::userToUserModelResponse).collect(Collectors.toList());

        return new PageResult<>(collect,
                userPageResult.getPageSize(),
                userPageResult.getPageNumber(),
                userPageResult.getOffset(),
                userPageResult.getTotal());
    }

    @Override
    public Page<UserModelResponse> getUserSearchWithCriteriaBuilder(Map<String, String> queryString, PagedQuery pagedQuery) {

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
            List<UserModelResponse> collect = userQueryWithCriteriaBuilder.get().map(userMapper::userToUserModelResponse).collect(Collectors.toList());
            return new PageImpl<>(collect, pageable, userQueryWithCriteriaBuilder.getTotalElements());
        }catch (RuntimeException exception) {
            throw AppException.newInstance(
                    SampleExceptionType.INTERNAL_ERROR,
                    String.format("getUserSearchWithCriteriaBuilder method has error: %s", exception.getCause())
            );
        }
    }
}
