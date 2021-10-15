package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.dto.PagedQuery;
import ir.bigz.springbootreal.viewmodel.UserModel;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {

    UserModel getUser(Long userId);

    UserModel addUser(UserModel userModel);

    UserModel updateUser(long userId, UserModel userModel);

    String deleteUser(long userId);

    List<UserModel> getAll();

    Page<UserModel> getUserSearchResult(UserSearchDto userSearchDto, String sortOrder, Sort.Direction direction, Integer pageNumber, Integer pageSize);

    Page<UserModel> getAllUserPage(String sortOrder, Sort.Direction sortDirection, Integer pageNumber, Integer pageSize);

    PageResult<UserModel> getUserSearchWithNativeQuery(Map<String, String> queryString, PagedQuery pagedQuery);

    Page<UserModel> getUserSearchWithCriteriaBuilder(Map<String, String> queryString, PagedQuery pagedQuery);
}
