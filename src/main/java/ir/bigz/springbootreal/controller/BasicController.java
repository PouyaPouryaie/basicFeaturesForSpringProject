package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.dao.User;
import ir.bigz.springbootreal.service.UserService;
import ir.bigz.springbootreal.viewmodel.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
    public User getUserById(@PathVariable("id") long id) {
        return userService.getUser(id);
    }

    @PostMapping(path = "/user/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody UserModel userModel) {
        return userService.addUser(userModel);
    }


    @DeleteMapping(path = "/user/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String deleteUser(@PathVariable("id") long userId) {
        return userService.deleteUser(userId);
    }

}
