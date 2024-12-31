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

    INTERNAL_SERVER_ERROR(2000, HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final HttpStatus httpStatus;
}
