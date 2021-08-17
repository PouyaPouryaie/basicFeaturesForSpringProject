package ir.bigz.springbootreal.dao.mapper;

import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModel userToUserModel(User user);
    User userModelToUser(UserModel userModel);
}
