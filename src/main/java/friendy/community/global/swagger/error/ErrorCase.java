package friendy.community.global.swagger.error;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target(value = METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorCase {

    String description();

    String exampleMessage();
}
