package friendy.community.global.exception;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

@Getter
public class FriendyException extends RuntimeException {

    private final ErrorCode errorCode;

    public FriendyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ProblemDetail toProblemDetail() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                errorCode.getHttpStatus(),
                getMessage());
        return GlobalExceptionHandler.setProperties(problemDetail, errorCode.getCode());
    }

}
