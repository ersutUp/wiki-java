package top.ersut.boot.conditional.condition.impl;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import sun.security.provider.ConfigFile;

import java.util.Arrays;


public class ConditionalByProd implements Condition {

    static final String PROD = "prod";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return Arrays.asList(context.getEnvironment().getActiveProfiles()).contains(PROD);
    }
}
