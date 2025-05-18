package org.virtualquest.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* org.virtualquest..*(..)) && !within(org.springframework.web.filter.OncePerRequestFilter+)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        log.debug("Method {} executed in {} ms",
                joinPoint.getSignature(),
                System.currentTimeMillis() - start);
        return result;
    }
}
