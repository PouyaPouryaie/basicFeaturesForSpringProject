package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.service.UserService;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.RollbackException;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/basic")
public class BasicController {

    Logger log = LoggerFactory.getLogger(BasicController.class);

    final
    UserService userService;

    public BasicController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/home", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getHome() {
        log.info("get request for /home");
        return "hello";
    }

    @GetMapping(path = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
        UserModel userModel = userService.getUser(id);
        if(Objects.nonNull(userModel)){
            return ResponseEntity.ok(userModel);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping(path = "/user/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addUser(@RequestBody UserModel userModel) {
        UserModel userModelResult = userService.addUser(userModel);
        if(Objects.nonNull(userModelResult)){
            return ResponseEntity.ok(userModelResult);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }


    @PostMapping(path = "/user/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId) {
        try{
            String result = userService.deleteUser(userId);
            if(result.equals("Success")){
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }catch (RuntimeException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
