package friendy.community.global.swagger.error;

import org.springframework.http.HttpStatus;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

@Target(value = METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiErrorResponses.class)
public @interface ApiErrorResponse {
    String type() default "about:blank";
    HttpStatus status();
    String instance();
    ErrorCase[] errorCases();
}
