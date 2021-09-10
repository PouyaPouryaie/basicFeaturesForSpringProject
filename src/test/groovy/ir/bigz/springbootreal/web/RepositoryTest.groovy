package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.configuration.DataSourceConfiguration
import ir.bigz.springbootreal.configuration.WebConfiguration
import ir.bigz.springbootreal.dal.UserRepository
import ir.bigz.springbootreal.dal.UserRepositoryImpl
import ir.bigz.springbootreal.dao.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Specification
import spock.lang.Title

import java.sql.Timestamp
import java.time.LocalDateTime

@ContextConfiguration(classes = [UserRepositoryImpl.class, User.class,
        DataSourceConfiguration.class, WebConfiguration.class])
@Title("Test repository layer")
@SpringBootTest(properties = "spring.profiles.active:test")
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@EnableTransactionManagement
class RepositoryTest extends Specification{

    @Autowired
    private UserRepository userRepository

    def "create user and add to db"(){

        given: "create fake use"
        def user = generateUser()

        when: "insert entity to db and assign id to entity"
        userRepository.insert(user)

        then: "entity has id, success persist"
        user.getId() != null

    }

    def "update user db"(){

        given: "create fake use"
        def user = generateUser()

        and: "insert user to db"
        userRepository.insert(user)

        when: "find user and update"
        def find = userRepository.find(user.getId())

        and: "update date"
        find.get().setUserName("sample2")
        find.get().setUpdateDate(Timestamp.valueOf(LocalDateTime.now()))
        userRepository.update(find.get())

        then: "if properties are updated so test is success"
        def result = userRepository.find(user.id)
        result.get().getUserName() == "sample2"

    }

    //before each test this command run
    def setup(){}

    //after each test this command run
    def cleanup(){
        userRepository.deleteAll()
    }

    private static User generateUser() {
        return new User(
                id: null,
                insertDate: Timestamp.valueOf(LocalDateTime.now()),
                updateDate: null,
                activeStatus: true,
                firstName: "pouya",
                lastName: "pouryaie",
                userName: "sample",
                nationalCode: "0014713225",
                mobile: "09388773155",
                email: "pouyapouryaie@gmail.com",
                gender: "man"
        )
    }
}
