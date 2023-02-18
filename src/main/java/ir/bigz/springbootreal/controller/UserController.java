package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.service.UserService;
import ir.bigz.springbootreal.viewmodel.UserModelRequest;
import ir.bigz.springbootreal.viewmodel.UserModelResponse;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserController extends AbstractController {

    final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/v1/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
        UserModelResponse userModelRequest = userService.getUser(id);
        return ResponseEntity.ok(userModelRequest);
    }

    @PostMapping(path = "/v1", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addUser(@Valid @RequestBody UserModelRequest userModelRequest) {
        UserModelResponse userModelRequestResult = userService.addUser(userModelRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModelRequestResult);
    }

    @PostMapping(path = "/v1/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateUser(@RequestBody UserModelRequest userModelRequest, @PathVariable("id") long userId) {
        UserModelResponse userModelRequestResult = userService.updateUser(userId, userModelRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userModelRequestResult);
    }


    @DeleteMapping(path = "/v1/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId) {
        String result = userService.deleteUser(userId);
        if (result.equals("Success")) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @GetMapping(path = "/v1", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllUser() {
        List<UserModelResponse> result = userService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserWithSearch(@RequestBody UserSearchDto userSearchDto,
                                               @RequestParam(name = "sortorder", defaultValue = "id") String sortOrder,
                                               @RequestParam(name = "direction", defaultValue = "desc", required = false) String direction,
                                               @RequestParam(name = "pagenumber", defaultValue = "1") Integer pageNumber,
                                               @RequestParam(name = "pagesize", defaultValue = "5") Integer pageSize) {
        Page<UserModelResponse> userPageResult = userService.getUserSearchResult(userSearchDto, sortOrder, Sort.Direction.fromString(direction), pageNumber, pageSize);
        return ResponseEntity.ok(userPageResult);
    }

    @GetMapping(path = "/v1/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllUserPage(@RequestParam(name = "sortorder", required = false) String sortOrder,
                                            @RequestParam(name = "direction") String sortDirection,
                                            @RequestParam(name = "pagenumber") Integer pageNumber,
                                            @RequestParam(name = "pagesize") Integer pageSize) {
        Page<UserModelResponse> userPageResult = userService.getAllUserPage(sortOrder, Sort.Direction.fromString(sortDirection), pageNumber, pageSize);
        return ResponseEntity.ok(userPageResult);
    }

    @GetMapping(path = "/v2/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserSearchWithNativeQuery() {
        PageResult<UserModelResponse> userPageResult = userService.getUserSearchWithNativeQuery(getQueryString(), getPagedQuery());
        return ResponseEntity.ok(userPageResult);
    }

    @GetMapping(path = "/v3/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserSearchWithCriteriaBuilder() {
        Page<UserModelResponse> userPageResult = userService.getUserSearchWithCriteriaBuilder(getQueryString(), getPagedQuery());
        return ResponseEntity.ok(userPageResult);
    }
}
