package Fridge_Chef.team.exception;


import java.util.Arrays;

/**
 * 에러 코드
 */
public enum ErrorCode {
    //Server
    INTERNAL_SERVER_ERROR(500, "서버 오류");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.message = message;
        this.status = status;
    }

    public static ErrorCode fromMessage(String message) {
        return Arrays.stream(ErrorCode.values())
                .filter(errorCode -> errorCode.getMessage().equals(message))
                .findFirst()
                .orElse(null);
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
