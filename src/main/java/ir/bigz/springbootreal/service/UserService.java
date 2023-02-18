package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.viewmodel.UserModelRequest;
import ir.bigz.springbootreal.viewmodel.UserModelResponse;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {

    UserModelResponse getUser(Long userId);

    UserModelResponse addUser(UserModelRequest userModelRequest);

    UserModelResponse updateUser(long userId, UserModelRequest userModelRequest);

    String deleteUser(long userId);

    List<UserModelResponse> getAll();

    Page<UserModelResponse> getUserSearchResult(UserSearchDto userSearchDto, String sortOrder, Sort.Direction direction, Integer pageNumber, Integer pageSize);

    Page<UserModelResponse> getAllUserPage(String sortOrder, Sort.Direction sortDirection, Integer pageNumber, Integer pageSize);

    PageResult<UserModelResponse> getUserSearchWithNativeQuery(Map<String, String> queryString, PagedQuery pagedQuery);

    Page<UserModelResponse> getUserSearchWithCriteriaBuilder(Map<String, String> queryString, PagedQuery pagedQuery);
}
