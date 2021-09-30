package ir.bigz.springbootreal.web

import ir.bigz.springbootreal.configuration.DataSourceConfiguration
import ir.bigz.springbootreal.configuration.HikariDataSourceInit
import ir.bigz.springbootreal.configuration.SimpleDataSourceInit
import ir.bigz.springbootreal.configuration.WebConfiguration
import ir.bigz.springbootreal.dal.DaoRepository
import ir.bigz.springbootreal.dal.DaoRepositoryImpl
import ir.bigz.springbootreal.dal.UserRepository
import ir.bigz.springbootreal.dal.UserRepositoryImpl
import ir.bigz.springbootreal.dao.BaseEntity
import ir.bigz.springbootreal.dao.User
import ir.bigz.springbootreal.datagenerator.DataGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.EnableTransactionManagement
import spock.lang.Shared
import spock.lang.Title

import javax.transaction.Transactional
import java.sql.Timestamp
import java.time.LocalDateTime

@ContextConfiguration(classes = [UserRepositoryImpl.class, User.class,
        DataSourceConfiguration.class, SimpleDataSourceInit.class, HikariDataSourceInit.class, WebConfiguration.class, DataGenerator.class])
@Title("Test repository layer")
@SpringBootTest(properties = "spring.profiles.active:test")
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@EnableTransactionManagement
class RepositoryTest extends InitTestContainerDB {

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

    @Transactional
    def "deactivate user with stream data and not throw exception"(){

        def countActiveUser = userRepository.getAll().filter(BaseEntity::isActiveStatus).count()

        when: "deactivate users"
        userRepository.getAll()
                .filter(BaseEntity::isActiveStatus).peek(user -> { user.setActiveStatus(false) }).count()

        then: "after deactivate user, count active must be zero"
        def count = userRepository.getAll().filter(BaseEntity::isActiveStatus).count()
        count == 0
        countActiveUser != count

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

    //exec once before class run test
    def setupSpec(){}

    //before each test this command run
    def setup(){
        DataGenerator dataGenerator = new DataGenerator(userRepository)
        dataGenerator.run()
    }

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
                gender: "male"
        )
    }
}
