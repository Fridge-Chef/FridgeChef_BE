package Fridge_Chef.team.exception;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private int status;
    private String message;

    public ErrorResponse(ErrorCode code) {
        this.status = code.getStatus();
        this.message = code.getMessage();
    }

    private ErrorResponse(int status, String message ) {
        this.status = status;
        this.message =message;
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }
    public static ErrorResponse of(int status,String message){
        return new ErrorResponse(status,message);
    }
}