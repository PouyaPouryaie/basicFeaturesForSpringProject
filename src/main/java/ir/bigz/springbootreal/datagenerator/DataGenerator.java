package ir.bigz.springbootreal.datagenerator;

import com.github.javafaker.Faker;
import ir.bigz.springbootreal.dal.UserRepository;
import ir.bigz.springbootreal.dto.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.sql.Timestamp;

@Component
@ConditionalOnProperty(name = "app.generator.enabled", havingValue = "true")
public class DataGenerator implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
                    user.setNationalCode(generateNationalCode(faker));
                    user.setActiveStatus(faker.bool().bool());
                    user.setGender(faker.bool().bool() ? "male" : "female");
                    user.setEmail(faker.bothify("????##@????.com"));
                    user.setInsertDate(new Timestamp(faker.date().between(createDateFromString("2000-01-01"),
                            createDateFromString("2020-10-20")).getTime()));
                    return user;
                }).collect(Collectors.toList());
        userList.forEach(userRepository::insert);
    }

    private String generateNationalCode(Faker faker){
        String randomSample = faker.regexify("[0-9]{9}0");
        char[] chars = randomSample.trim().toCharArray();
        int[] ints = new int[10];
        int index = 0;
        for (char c: chars){
            int i = Integer.parseInt(String.valueOf(c));
            ints[index] = i;
            index++;
        }
        int sum = 0;
        for(int i: ints){
            sum += i * index;
            index--;
        }
        int result  = 0 ;
        int s = sum % 11;
        if(s < 2){
            result = s;
        }
        else {
            result = 11 - s;
        }

        StringBuffer buffer = new StringBuffer(randomSample);
        StringBuffer nationalCodeSample = buffer.replace(9,10, String.valueOf(result));
        return nationalCodeSample.toString();
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
