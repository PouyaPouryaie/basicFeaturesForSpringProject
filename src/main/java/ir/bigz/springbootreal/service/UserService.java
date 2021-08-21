package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserModel getUser(Long userId);

    UserModel addUser(UserModel userModel);

    UserModel updateUser(long userId, UserModel userModel);

    String deleteUser(long userId);

    List<UserModel> getAll();

    Page<UserModel> getUserSearchResult(Integer pageNumber, Integer pageSize, String sortOrder);
}
