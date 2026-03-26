package com.faus535.englishtrainer.shared.infrastructure.security;

import com.faus535.englishtrainer.user.infrastructure.controller.ProfileOwnershipChecker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.UUID;

@Aspect
@Component
class ProfileOwnershipAspect {

    @Around("@annotation(requireProfileOwnership)")
    Object checkOwnership(ProceedingJoinPoint joinPoint, RequireProfileOwnership requireProfileOwnership)
            throws Throwable {
        String targetPathVariable = requireProfileOwnership.pathVariable();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = joinPoint.getArgs();

        UUID profileId = null;
        for (int i = 0; i < parameters.length; i++) {
            PathVariable pathVariable = findAnnotation(parameters[i], PathVariable.class);
            if (pathVariable != null) {
                String name = pathVariable.value().isEmpty() ? parameters[i].getName() : pathVariable.value();
                if (name.equals(targetPathVariable) && args[i] instanceof UUID uuid) {
                    profileId = uuid;
                    break;
                }
            }
        }

        if (profileId == null) {
            throw new IllegalStateException(
                    "@RequireProfileOwnership: no @PathVariable UUID parameter named '" + targetPathVariable + "' found");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ProfileOwnershipChecker.check(authentication, profileId);

        return joinPoint.proceed();
    }

    @SuppressWarnings("unchecked")
    private <A extends Annotation> A findAnnotation(Parameter parameter, Class<A> annotationType) {
        for (Annotation annotation : parameter.getAnnotations()) {
            if (annotationType.isInstance(annotation)) {
                return (A) annotation;
            }
        }
        return null;
    }
}
