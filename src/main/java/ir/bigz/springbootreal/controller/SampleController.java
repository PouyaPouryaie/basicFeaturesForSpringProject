package ir.bigz.springbootreal.controller;

import ir.bigz.springbootreal.dto.PageResult;
import ir.bigz.springbootreal.messages.MessageContainer;
import ir.bigz.springbootreal.service.UserService;
import ir.bigz.springbootreal.viewmodel.UserModel;
import ir.bigz.springbootreal.viewmodel.search.UserSearchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class SampleController extends AbstractController{

    final UserService userService;

    final MessageSource source;

    @Autowired
    @Qualifier("loadErrorMessageSource")
    ReloadableResourceBundleMessageSource loadMessageSource;

    public SampleController(UserService userService, MessageSource source) {
        this.userService = userService;
        this.source = source;
    }

    @GetMapping("/v1/geterror")
    public ResponseEntity<?> getErrorMessage(
            @RequestHeader(name = "Accept-Language", required = false) final Locale locale) {
        return ResponseEntity.ok(loadMessageSource.getMessage("Exception", new Object[0], locale));
    }

    @GetMapping("/v1/welcome")
    public ResponseEntity<?> getLocaleMessage(
            @RequestHeader(name = "Accept-Language", required = false) final Locale locale,
            @RequestParam(name = "username", defaultValue = "Java Geek", required = false) final String username) {
        MessageContainer messageContainer = MessageContainer.create();
        messageContainer.add("welcome.message", new Object[]{username});
        return getSuccessMessage(source, messageContainer, locale);
    }


    @GetMapping(path = "/v1/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserById(@PathVariable("id") long id) {
        UserModel userModel = userService.getUser(id);
        return ResponseEntity.ok(userModel);
    }

    @PostMapping(path = "/v1/user/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> addUser(@Valid @RequestBody UserModel userModel) {
        UserModel userModelResult = userService.addUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userModelResult);
    }

    @PostMapping(path = "/v1/user/update/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> updateUser(@RequestBody UserModel userModel, @PathVariable("id") long userId) {
        UserModel userModelResult = userService.updateUser(userId, userModel);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userModelResult);
    }


    @PostMapping(path = "/v1/user/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId) {
        String result = userService.deleteUser(userId);
        if(result.equals("Success")){
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @GetMapping(path = "/v1/user/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllUser() {
        List<UserModel> result = userService.getAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/v1/user/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserWithSearch(@RequestBody UserSearchDto userSearchDto,
                                               @RequestParam(name = "sortorder", defaultValue = "id") String sortOrder,
                                               @RequestParam(name = "direction", defaultValue = "desc", required = false) String direction,
                                               @RequestParam(name = "pagenumber", defaultValue = "1") Integer pageNumber,
                                               @RequestParam(name = "pagesize", defaultValue = "5") Integer pageSize) {
        Page<UserModel> userPageResult = userService.getUserSearchResult(userSearchDto, sortOrder, Sort.Direction.fromString(direction), pageNumber, pageSize);
        return ResponseEntity.ok(userPageResult);
    }

    @GetMapping(path = "/v1/user/all/pagerquest", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllUserPage(@RequestParam(name = "sortorder", required = false) String sortOrder,
                                            @RequestParam(name = "direction") String sortDirection,
                                            @RequestParam(name = "pagenumber") Integer pageNumber,
                                            @RequestParam(name = "pagesize") Integer pageSize) {
        Page<UserModel> userPageResult = userService.getAllUserPage(sortOrder, Sort.Direction.fromString(sortDirection), pageNumber, pageSize);
        return ResponseEntity.ok(userPageResult);
    }

    @GetMapping(path = "/v2/user/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserSearchWithNativeQuery() {
        PageResult<UserModel> userPageResult = userService.getUserSearchWithNativeQuery(getQueryString(), getPagedQuery());
        return ResponseEntity.ok(userPageResult);
    }

    @GetMapping(path = "/v3/user/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getUserSearchWithCriteriaBuilder() {
        Page<UserModel> userPageResult = userService.getUserSearchWithCriteriaBuilder(getQueryString(), getPagedQuery());
        return ResponseEntity.ok(userPageResult);
    }

}
