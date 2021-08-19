package ir.bigz.springbootreal.commons.generallog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AppLogAspect {

    private final Logger LOG = LoggerFactory.getLogger(AppLogAspect.class);

    @Before("ir.bigz.springbootreal.commons.generallog.CommonJoinPoint.ControllerExecution()")
    public void before(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String reduce = Arrays.stream(args).reduce("", (s, s2) -> s + " " + s2).toString();
        LOG.info("before method: {} | argument: {}",
                methodName, reduce);
    }

    @AfterReturning(value = "ir.bigz.springbootreal.commons.generallog.CommonJoinPoint.ControllerExecution()",
            returning = "obj")
    public void afterReturning(JoinPoint joinPoint, Object obj){
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String reduce = Arrays.stream(args).reduce("", (s, s2) -> s + " " + s2).toString();
        LOG.info("after method: {} | argument: {} | result: {}", methodName, reduce, ((ResponseEntity) obj).getBody());
    }
}
