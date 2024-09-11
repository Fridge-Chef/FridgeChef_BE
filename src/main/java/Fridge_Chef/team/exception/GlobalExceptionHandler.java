package Fridge_Chef.team.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static Pattern pattern = Pattern.compile("default message \\[([^\\]]+)]");


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handlerServerException(Exception e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.of(
                e.getStatusCode().value()
                , extractMessage(e.getMessage()));
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }


    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ErrorResponse> handlerApiException(ApiException e) {
        ErrorResponse errorResponse = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handlerIllegalArgumentExceptionException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.INVALID_VALUE);
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    public static String extractMessage(String error) {
        Matcher matcher = pattern.matcher(error);

        int count = 0;
        while (matcher.find()) {
            if (count++ == 2) {
                return matcher.group(1);
            }
        }
        return null;
    }

}
