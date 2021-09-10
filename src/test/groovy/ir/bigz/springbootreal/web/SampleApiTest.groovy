package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.commons.util.Utils
import ir.bigz.springbootreal.configuration.CacheConfiguration
import ir.bigz.springbootreal.configuration.DataSourceConfiguration
import ir.bigz.springbootreal.configuration.WebConfiguration
import ir.bigz.springbootreal.controller.SampleController
import ir.bigz.springbootreal.dal.UserRepository
import ir.bigz.springbootreal.dao.User
import ir.bigz.springbootreal.dao.mapper.UserMapper
import ir.bigz.springbootreal.dao.mapper.UserMapperImpl
import ir.bigz.springbootreal.exception.validation.ErrorController
import ir.bigz.springbootreal.exception.validation.ValidationErrorResponseModel
import ir.bigz.springbootreal.service.UserService
import ir.bigz.springbootreal.service.UserServiceImpl
import ir.bigz.springbootreal.validation.*
import ir.bigz.springbootreal.validation.annotation.Validator
import ir.bigz.springbootreal.viewmodel.UserModel
import org.junit.jupiter.api.Test
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@ContextConfiguration(classes = [SampleController.class, UserServiceImpl.class, UserModel.class, User.class,
        UserMapper.class, UserMapperImpl.class, UserRepository.class,DataSourceConfiguration.class,
        WebConfiguration.class, CacheConfiguration.class,
        ValidationHandler.class, ValidationUtilsImpl.class, ValidationValidator.class,
        ValidationErrorResponseModel.class,ErrorController.class, ValidationType.class])
@Title("sample controller mock test")
@SpringBootTest(properties = "spring.profiles.active:test", webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@EnableTransactionManagement
class SampleApiTest extends Specification{

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    private ValidationUtils validationUtils

    @Autowired
    ErrorController errorController

    @Autowired
    ValidationValidator validationValidator

    @Autowired
    ValidationHandler validationHandler

    @Autowired
    @Subject
    private SampleController sampleController

    @SpringBean
    UserService userService = Stub(UserService.class)

    @SpringBean
    UserRepository userRepository = Stub(UserRepository.class)

    @Autowired
    private TestRestTemplate restTemplate

    void setup(){}

    @Test
    def "if getAll request return ok"(){

        given:"create mock userModelList"
        def list = generateUserList()

        and:"define behavior of userService.getAll method"
        userService.getAll() >> list

        when:"call endpoint"
        def exchange = restTemplate.exchange("/api/v1/user/all",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<UserModel>>() {
        })

        then:"expected return result"
        exchange.getStatusCode() == HttpStatus.OK
    }

    @Test
    def "if create user and return model then ok"(){

        given:"create mock user and userModel"
        def user = generateUser()
        def model = generateUserModel()

        and:"define behavior of userRepository methods"
        userRepository.insert(_) >> user
        userRepository.getUserWithNationalCode(_) >> null

        and:"create entity request"
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        HttpEntity<UserModel> request = new HttpEntity<>(model, headers)

        when:"call endpoint"
        def response = restTemplate.postForEntity("/api/v1/user/add",
                request,
                String.class)

        then:"expected return result"
        response.getStatusCode() == HttpStatus.OK
    }


    private static UserModel generateUserModel() {
        Random random = new Random()
        new UserModel(
                id: random.nextInt(1000),
                version: null,
                insertDate: Utils.getLocalTimeNow(),
                updateDate: null,
                activeStatus: true,
                firstName: "first",
                lastName: "last",
                userName: "sample",
                nationalCode: "0014713225",
                mobile: "09388773155",
                email: "pouyapouryaie@gmail.com",
                gender: "man"
        )
    }

    private static User generateUser() {
        Random random = new Random()
        new User(
                id: random.nextInt(1000),
                activeStatus: true,
                insertDate: Utils.getTimestampNow(),
                updateDate: null,
                userName: "sample",
                firstName: "first",
                lastName: "last",
                nationalCode: '0014713225',
                mobile: '09388773155',
                gender: "man"
        )
    }

    private static List<UserModel> generateUserList() {
        List<UserModel> userModels = new ArrayList<>()
        UserModel userModel = generateUserModel()
        userModels.add(userModel)
        return userModels
    }

}
