package com.example.tryme.aspect;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.tryme.services.RequestCounter;

@Aspect
@Component
public class RequestCounterAspect {
    private final RequestCounter requestCounter;

    public RequestCounterAspect(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    @Pointcut("execution(* com.example.tryme.services.MealService.*(..))")
    public void mealServiceMethods() {}

    @Before("mealServiceMethods()")
    public void countAllRequests() {
        requestCounter.incrementTotal();
    }

    @AfterReturning(pointcut = "mealServiceMethods()", returning = "result")
    public void countSuccessfulRequests(Object result) {
        requestCounter.incrementSuccessful();
    }

    @AfterThrowing(pointcut = "mealServiceMethods()", throwing = "ex")
    public void countFailedRequests(Exception ex) {
        requestCounter.incrementFailed();
    }
}