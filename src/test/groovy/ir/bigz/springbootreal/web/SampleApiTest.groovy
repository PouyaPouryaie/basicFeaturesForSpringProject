package ir.bigz.springbootreal.web

import com.fasterxml.jackson.databind.ObjectMapper
import ir.bigz.springbootreal.commons.util.Utils
import ir.bigz.springbootreal.controller.SampleController
import ir.bigz.springbootreal.dal.UserRepository
import ir.bigz.springbootreal.dao.User
import ir.bigz.springbootreal.service.UserService
import ir.bigz.springbootreal.viewmodel.UserModel
import org.junit.jupiter.api.Test
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification
import spock.lang.Title

@Title("sample controller mock test")
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@EnableTransactionManagement
@ComponentScan("ir.bigz.springbootreal")
@WebMvcTest(properties = "spring.profiles.active:test")
class SampleApiTest extends Specification{

    @Autowired
    private WebApplicationContext webApplicationContext

    @Autowired
    private SampleController sampleController

    @SpringBean
    UserService userService = Stub(UserService.class)

    @SpringBean
    UserRepository userRepository = Stub(UserRepository.class)

    private MockMvc mockMvc

    void setup(){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build()
    }

    @Test
    def "if getAll request return ok"(){

        given:"create mock userModelList"
        def list = generateUserList()

        and:"define behavior of userService.getAll method"
        userService.getAll() >> list

        when:"call endpoint"

        def result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/user/all")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:"expected return result"
        result.getResponse().getStatus() == HttpStatus.OK.value()
    }

    @Test
    def "if create user and return model then ok"(){

        given:"create mock user and userModel"
        def user = generateUser()
        def model = generateUserModel()

        and:"define behavior of userRepository methods"
        userRepository.insert(_) >> user
        userRepository.getUserWithNationalCode(_) >> null

        and:"create json Object"
        ObjectMapper objectMapper = new ObjectMapper()
        def json = objectMapper.writeValueAsString(model)

        when:"call endpoint"
        def expect = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn()

        then:"expected return result"
        expect.getResponse().getStatus() == HttpStatus.CREATED.value()
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
                gender: "male"
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
                gender: "male"
        )
    }

    private static List<UserModel> generateUserList() {
        List<UserModel> userModels = new ArrayList<>()
        UserModel userModel = generateUserModel()
        userModels.add(userModel)
        return userModels
    }

}
