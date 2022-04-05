package ir.bigz.springbootreal.configuration;

import ir.bigz.springbootreal.service.AlertServiceJob;
import ir.bigz.springbootreal.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class BeanConfig {

    @Bean("taskScheduler")
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);
        return scheduler;
    }

    @Bean("alertServiceJob")
    public AlertServiceJob alertServiceJob(UserService userService){
        return new AlertServiceJob(userService);
    }
}
