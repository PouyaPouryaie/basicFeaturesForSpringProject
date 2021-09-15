package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.service.UserService;
import ir.bigz.springbootreal.viewmodel.UserModel;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class SampleController {

    final
    UserService userService;

    public SampleController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
        UserModel userModel = userService.getUser(id);
        return ResponseEntity.ok(userModel);
    }

    @PostMapping(path = "/user/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addUser(@Valid @RequestBody UserModel userModel) {
        UserModel userModelResult = userService.addUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModelResult);
    }

    @PostMapping(path = "/user/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateUser(@RequestBody UserModel userModel, @PathVariable("id") long userId) {
        UserModel userModelResult = userService.updateUser(userId, userModel);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userModelResult);
    }


    @PostMapping(path = "/user/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId) {
        String result = userService.deleteUser(userId);
        if(result.equals("Success")){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @GetMapping(path = "/user/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllUser() {
        List<UserModel> result = userService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/user/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserWithSearch(@RequestBody UserSearchDto userSearchDto,
                                               @RequestParam(name = "sortorder", defaultValue = "id") String sortOrder,
                                               @RequestParam(name = "direction", defaultValue = "desc", required = false) String direction,
                                               @RequestParam(name = "pagenumber", defaultValue = "1") Integer pageNumber,
                                               @RequestParam(name = "pagesize", defaultValue = "5") Integer pageSize) {
        Page<UserModel> userPageResult = userService.getUserSearchResult(userSearchDto, sortOrder, Sort.Direction.fromString(direction), pageNumber, pageSize);
        return ResponseEntity.ok(userPageResult);
    }


    @GetMapping(path = "/user/all/pagerquest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllUserPage(@RequestParam(name = "sortorder", required = false) String sortOrder,
                                            @RequestParam(name = "direction") String sortDirection,
                                            @RequestParam(name = "pagenumber") Integer pageNumber,
                                            @RequestParam(name = "pagesize") Integer pageSize) {
        Page<UserModel> userPageResult = userService.getAllUserPage(sortOrder, Sort.Direction.fromString(sortDirection), pageNumber, pageSize);
        return ResponseEntity.ok(userPageResult);
    }

}
