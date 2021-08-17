package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserModel getUser(Long userId);

    UserModel addUser(UserModel userModel);

    String deleteUser(long userId);

    List<UserModel> getAll();
}
