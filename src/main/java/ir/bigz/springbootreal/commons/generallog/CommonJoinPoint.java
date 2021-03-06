package ir.bigz.springbootreal.commons.generallog;

import org.aspectj.lang.annotation.Pointcut;

public class CommonJoinPoint {

    @Pointcut("execution(* ir.bigz.springbootreal.controller.*.*(..))")
    public void controllerExecution(){};

    @Pointcut("execution(* ir.bigz.springbootreal.service.*.*(..))")
    public void serviceExecution(){};
}
