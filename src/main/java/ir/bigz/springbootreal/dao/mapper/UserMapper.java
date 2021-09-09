package ir.bigz.springbootreal.dao.mapper;

import ir.bigz.springbootreal.commons.util.Utils;
import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(source = "insertDate", target = "insertDate", qualifiedByName = "timestampToStringMapper"),
            @Mapping(source = "updateDate", target = "updateDate", qualifiedByName = "timestampToStringMapper")
    })
    UserModel userToUserModel(User user);

    @Mappings({
            @Mapping(source = "insertDate", target = "insertDate", qualifiedByName = "timeStringToTimestampMapper"),
            @Mapping(source = "updateDate", target = "updateDate", qualifiedByName = "timeStringToTimestampMapper")
    })
    User userModelToUser(UserModel userModel);

    @Named("timestampToStringMapper")
    static String timestampToStringMapper(Timestamp timestamp){
        if(Objects.nonNull(timestamp) && !timestamp.toString().isBlank())
            return Utils.convertTimestamp(timestamp);
        return "";
    }

    @Named("timeStringToTimestampMapper")
    static Timestamp timeStringToTimestampMapper(String time){
        if(Objects.nonNull(time) && !time.isBlank())
            return Utils.convertTimeString(time);
        return null;
    }
}
