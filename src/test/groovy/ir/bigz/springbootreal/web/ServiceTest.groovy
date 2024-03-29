package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.configuration.DataSourceConfiguration
import ir.bigz.springbootreal.configuration.WebConfiguration
import ir.bigz.springbootreal.dal.UserRepository
import ir.bigz.springbootreal.dal.UserRepositoryImpl
import ir.bigz.springbootreal.datagenerator.DataGenerator
import ir.bigz.springbootreal.dto.PagedQuery
import ir.bigz.springbootreal.dto.entity.User
import ir.bigz.springbootreal.dto.mapper.UserMapper
import ir.bigz.springbootreal.dto.mapper.UserMapperImpl
import ir.bigz.springbootreal.service.UserService
import ir.bigz.springbootreal.service.UserServiceImpl
import ir.bigz.springbootreal.viewmodel.UserModelRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Title

@ContextConfiguration(classes = [UserRepositoryImpl.class, User.class,
        DataSourceConfiguration.class, WebConfiguration.class, UserServiceImpl.class, UserModelRequest.class, UserMapper.class, UserMapperImpl.class, DataGenerator.class])
@Title("Test service layer")
@SpringBootTest(properties = "spring.profiles.active:test")
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@EnableTransactionManagement
class ServiceTest extends InitTestContainerDB {

    @Autowired
    private UserRepository userRepository

    @Autowired
    private UserService userService

    def "insert data from service"(){

        given: "create model"
        def model = generateUserModel()

        when: "call service"
        def user = userService.addUser(model)

        then: "return data has id"
        user.getId() != null

    }

    def "update data from service"(){

        given: "create model"
        def model = generateUserModel()

        when: "call service first for insert"
        def user = userService.addUser(model)

        and: "get entity and update"
        userService.updateUser(user.getId(), generateUserModelForUpdate())
        def result = userService.getUser(user.getId())

        then: "return data has been changed"
        result.getId() != null
        result.getUpdateDate() != null

    }

    def "check getUserSearchV2 method is worked"(){
        given: "create pagedQuery"
        Map<String, Object> pageParams = new HashMap<>()
        pageParams.put("size", "10")
        pageParams.put("page", "1")
        pageParams.put("orderBy", "firstName_asc, gender_desc")
        PagedQuery pagedQuery = new PagedQuery(pageParams)

        and: "create queryString"
        Map<String, Object> queryParams = new HashMap<>()
        queryParams.put("firstName", "h")

        when: "call method"
        def result = userService.getUserSearchWithNativeQuery(queryParams, pagedQuery)

        then: "check result size"
        result.getResult().size() > 0
    }

    //before each test this command run
    def setup(){
        DataGenerator dataGenerator = new DataGenerator(userRepository)
        dataGenerator.run()
    }

    def cleanup(){
        userRepository.deleteAll()
    }

    private static UserModelRequest generateUserModel() {
        return new UserModelRequest(
                id: null,
                insertDate: null,
                updateDate: null,
                activeStatus: null,
                firstName: "pouya",
                lastName: "pouryaie",
                userName: "sample",
                nationalCode: "0014713225",
                mobile: "09388773155",
                email: "pouyapouryaie@gmail.com",
                gender: "male"
        )
    }

    private static UserModelRequest generateUserModelForUpdate() {
        return new UserModelRequest(
                id: null,
                insertDate: null,
                updateDate: null,
                activeStatus: false,
                firstName: "pouya",
                lastName: "pouryaie",
                userName: "sample2",
                nationalCode: "0014713225",
                mobile: "09122930872",
                email: "pouyapouryaie@gmail.com",
                gender: "male"
        )
    }
}
