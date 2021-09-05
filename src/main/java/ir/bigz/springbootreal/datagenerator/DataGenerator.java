package ir.bigz.springbootreal.datagenerator;

import com.github.javafaker.Faker;
import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dao.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@ConditionalOnProperty(name = "app.generator.enabled", havingValue = "true")
public class DataGenerator implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        Faker faker = new Faker();

        createUser(faker);
    }

    private void createUser(Faker faker){
        List<User> userList = IntStream.range(0, 100)
                .mapToObj(i -> {
                    User user = new User();
                    user.setFirstName(faker.name().firstName());
                    user.setLastName(faker.name().lastName());
                    user.setUserName(faker.name().username());
                    user.setMobile(faker.regexify("09[0-9]{9}"));
                    user.setNationalCode(faker.regexify("[0-9]{10}"));
                    if(faker.bool().bool()){
                        user.setGender("man");
                    }
                    else{
                        user.setGender("woman");
                    }
                    user.setEmail(faker.bothify("????##@????.com"));
                    faker.date().between(createDateFromString("2000-01-01"),
                            createDateFromString("2020-10-20"));
                    return user;
                }).collect(Collectors.toList());
        userList.forEach(user -> userRepository.insert(user));
    }


    private Date createDateFromString(String date){
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        Date result = null;
        try {
            result = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
