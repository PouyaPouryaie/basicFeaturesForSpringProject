package ir.bigz.springbootreal.service;

import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    User getUser(long userId);

    User addUser(UserModel userModel);

    String deleteUser(long userId);
}
