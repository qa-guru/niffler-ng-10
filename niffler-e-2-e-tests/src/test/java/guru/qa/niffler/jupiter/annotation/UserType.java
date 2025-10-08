package guru.qa.niffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserType {
    Type empty() default Type.EMPTY;

    enum Type{
        EMPTY, WHITH_FRIEND, WHITH_INCOME_REQUEST,WITH_OUTCOME_REQUEST
    }

}