package ir.bigz.springbootreal.dto.mapper;

import ir.bigz.springbootreal.commons.util.Utils;
import ir.bigz.springbootreal.dto.entity.User;
import ir.bigz.springbootreal.viewmodel.UserModelRequest;
import ir.bigz.springbootreal.viewmodel.UserModelResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.sql.Timestamp;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserModelResponse userToUserModelResponse(User user);

    @Mappings({
            @Mapping(source = "insertDate", target = "insertDate", qualifiedByName = "timeStringToTimestampMapper"),
            @Mapping(source = "updateDate", target = "updateDate", qualifiedByName = "timeStringToTimestampMapper")
    })
    User userModelToUser(UserModelRequest userModelRequest);

    @Named("timestampToStringMapper")
    static String timestampToStringMapper(Timestamp timestamp){
        if(Objects.nonNull(timestamp) && !timestamp.toString().isBlank())
            return Utils.convertTimestamp(timestamp);
        return null;
    }

    @Named("timeStringToTimestampMapper")
    static Timestamp timeStringToTimestampMapper(String time){
        if(Objects.nonNull(time) && !time.isBlank())
            return Utils.convertTimeString(time);
        return null;
    }
}
