package com.charter.rewardpoints.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {

    public static final Logger LOGGER = LogManager.getLogger(LoggingAspect.class);

    @AfterThrowing(pointcut = "execution(* com.charter.rewardpoints.controller.*.*(..))", throwing = "exception")
    public void logControllerException(JoinPoint joinPoint, Exception exception) {
        String method = getMethodName(joinPoint);
        LOGGER.error("CONTROLLER Exception in {}: {}", method, exception.getMessage(), exception);
    }

    @AfterThrowing(pointcut = "execution(* com.charter.rewardpoints.service.*.*(..))", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Exception exception) {
        String method = getMethodName(joinPoint);
        if (exception instanceof InterruptedException) {
            LOGGER.warn("SERVICE Thread interrupted in {}", method, exception);
            restoreInterruptStatus();
        } else {
            LOGGER.error("SERVICE Exception in {}: {}", method, exception.getMessage(), exception);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.charter.rewardpoints.repository.*.*(..))", throwing = "exception")
    public void logRepositoryException(JoinPoint joinPoint, Exception exception) {
        String method = getMethodName(joinPoint);
        LOGGER.error("REPOSITORY Exception in {}: {}", method, exception.getMessage(), exception);
    }

    @AfterThrowing(pointcut = "execution(* com.charter.rewardpoints.utility.RewardValidator.*(..))", throwing = "exception")
    public void logValidationException(JoinPoint joinPoint, Exception exception) {
        String method = getMethodName(joinPoint);
        LOGGER.error("VALIDATION Exception in {}: {}", method, exception.getMessage(), exception);
    }

    private String getMethodName(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        return className + "." + methodName;
    }

    private void restoreInterruptStatus() {
        Thread.currentThread().interrupt();
    }

}