package friendy.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(1101, HttpStatus.BAD_REQUEST),

    DUPLICATE_EMAIL(1201, HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME(1202, HttpStatus.CONFLICT),

    UNAUTHORIZED_USER(1301, HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_EMAIL(1302, HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_PASSWORD(1303, HttpStatus.UNAUTHORIZED),

    POST_NOT_FOUND(1401, HttpStatus.NOT_FOUND),
    UNAUTHORIZED_ACCESS(1402, HttpStatus.UNAUTHORIZED),
    INTERNAL_SERVER_ERROR(2000, HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final HttpStatus httpStatus;
}
