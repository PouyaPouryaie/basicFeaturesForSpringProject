package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.controller.SampleController
import ir.bigz.springbootreal.dao.User
import ir.bigz.springbootreal.service.UserService
import ir.bigz.springbootreal.service.UserServiceImpl
import ir.bigz.springbootreal.viewmodel.UserModel
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Title

@ContextConfiguration(classes = [SampleController.class, UserService.class, UserServiceImpl.class, UserModel.class, User.class])
@Title("sample spock test for testing project")
@ActiveProfiles("test")
class SampleApiTest extends Specification{

//    @Autowired
//    ApplicationContext applicationContext

    @Autowired
    @Subject
    private SampleController sampleController

    @SpringBean
    UserService userService = Stub(UserService.class)

    private TestRestTemplate template

    void setup(){}

    def "if find user by id is ok"(){

        given:"create mock userModel"
        UserModel user = generateUser()

        and:"define behavior of userService.getUser method"
        userService.getUser(_) >> user

        when:"call endpoint"
        def code = template.getForEntity("http://localhost:9090/api/v1//user/10", ResponseEntity<UserModel>.class as Class<Object>).statusCode

        then:"expected return result"
        code.value() == "200"
    }


    private static UserModel generateUser() {
        new UserModel(
                id: UUID.randomUUID(),
                userName: "sample",
                firstName: "first",
                lastName: "last",
                nationalCode: '0014713225',
                mobile: '09388773155',
                gender: "man"
        )
    }

}
