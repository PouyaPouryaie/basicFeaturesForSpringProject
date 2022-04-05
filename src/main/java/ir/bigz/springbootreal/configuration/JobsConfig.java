package ir.bigz.springbootreal.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.support.CronTrigger;

@Slf4j
@Configuration
@EnableScheduling
public class JobsConfig {

    public JobsConfig(ApplicationContext applicationContext,
                      @Qualifier("taskScheduler") TaskScheduler taskScheduler,
                      AppProperties appProperties) {
        for (var e: appProperties.getJobs().entrySet()){
            var bean = (Runnable) applicationContext.getBean(e.getKey());
            if(bean == null){
                log.error("Job {} is not registered", e.getKey());
            }
            else {
                log.info("Job {} is registered as cron {}", e.getKey(), e.getValue());
                taskScheduler.schedule(bean::run, new CronTrigger(e.getValue()));
            }
        }
    }
}
