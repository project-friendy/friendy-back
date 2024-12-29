package friendy.community.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(1101, HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME(1201, HttpStatus.CONFLICT);

    private final int code;
    private final HttpStatus httpStatus;
}
