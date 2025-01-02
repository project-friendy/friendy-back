package friendy.community.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ProblemDetail> handleFriendyException(FriendyException friendyException) {
        log.warn("[FriendyException] {}: {}", friendyException.getClass().getName(), friendyException.getMessage());

        return ResponseEntity.status(friendyException.getErrorCode().getHttpStatus())
                .body(friendyException.toProblemDetail());
    }

    public ResponseEntity<ProblemDetail> handleNonAuthorizedException(UnAuthorizedException exception) {
        FriendyException friendyException =
                new FriendyException(ErrorCode.UNAUTHORIZED_USER, exception.getMessage());

        return ResponseEntity.status(friendyException.getErrorCode().getHttpStatus())
                .body(friendyException.toProblemDetail());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        log.warn("[MethodArgumentNotValidException] {}: {}", exception.getClass().getName(), exception.getMessage());

        BindingResult bindingResult = exception.getBindingResult();
        List<String> errorMessages = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        FriendyException friendyException =
                new FriendyException(ErrorCode.INVALID_REQUEST, String.join("\n", errorMessages));

        return ResponseEntity.status(friendyException.getErrorCode().getHttpStatus())
                .body(friendyException.toProblemDetail());
    }

    public static ProblemDetail setProperties(ProblemDetail problemDetail, int code) {
        problemDetail.setProperty("errorCode", code);
        problemDetail.setProperty("timestamp", LocalDateTime.now().toString());

        return problemDetail;
    }

}
