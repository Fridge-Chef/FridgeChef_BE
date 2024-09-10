package Fridge_Chef.team.exception;


import java.util.Arrays;

/**
 * 에러 코드
 */
public enum ErrorCode {
    //Server
    INTERNAL_SERVER_ERROR(500, "서버 오류"),
    INVALID_VALUE(400, "유효하지 않은 값 입니다."),

    //security
    SECURITY_AUTHENTICATION_METHOD_NOT_SUPPORTED(450, "인증 방법이 지원되지 않습니다."),
    SECURITY_MESSAGE(451, ""),

    //image
    IMAGE_REMOTE_UPLOAD(401, "이미지 원격 업로드 실패"),
    IMAGE_REMOTE_SESSION(402, "이미지 세션 오류"),
    IMAGE_REMOTE_DELETE_FAIL(403, "이미지 삭제 실패"),
    IMAGE_NOT_ID(404, "이미지의 고유한 ID 가 존재하지 않음"),
    IMAGE_AUTHOR_MISMATCH(404, "이미지의 작성자가 일치하지 않음"),

    //USER
    USER_EMAIL_UNIQUE(401, "이메일 중복 에러"),
    USER_ID_DUPLICATE(403, "아이디 중복 에러"),
    USER_NOT_FOUND(404, "유저 정보 없음"),
    USER_NOT_EMAIL(405, "이메일 정보 없음"),

    //email
    EMAIL_SEND_PARSE(401, "메시지 구문 분석에 실패"),
    EMAIL_SEND_AUTHENTICATION(402, "인증이 실패"),
    EMAIL_SEND(403, "메시지 전송에 실패"),


    //signup
    SIGNUP_SMS_EXCEED(401, "회원가입 휴대폰 인증 횟수 초과"),
    SIGNUP_SMS_VERIFY_CODE_FAILED(402, "휴대폰 문자 인증 실패"),
    SIGNUP_SMS_DUPLICATE(403, "회원가입 휴대폰 번호 중복"),
    SIGNUP_EMAIL_EXCEED(404, "회원가입 이메일 인증 횟수 초과"),
    SIGNUP_EMAIL_VERIFY_CODE_FAILED(405, "이메일 인증 실패"),
    SIGNUP_EMAIL_DUPLICATE(402, "회원가입 이메일 중복"),
    SIGNUP_CERT_CODE_UNVERIFIED(401, "회원가입 인증코드 미인증"),
    TOKEN_ACCESS_FAIL(401, "로그인 토큰 복호화 실패"),
    TOKEN_ACCESS_EXPIRED_FAIL(401, "토큰값 만료"),
    PASSWORD_LEN_8_must(400, "패스워드 길이 부족"),
    LOGIN_PASSWORD_INCORRECT(400, "로그인 패스워드 불일치"),
    TOKEN_ACCESS_NOT_USER(400, "토큰값 유저정보 없음"),
    USER_ACCOUNT_DELETE(405, "이미 탈퇴 요청한 사용자 입니다. "),
    USER_ACCOUNT_DELETE_NAME_INCORRECT(406, "회원탈퇴 요청중 이름을 다르게 입력"),
    USER_PASSWORD_INPUT_FAIL(401, "기존 비밀번호 틀림"),
    USER_NEW_PASSWORD_SAME_AS_OLD(402, "사용자 새 비밀번호가 이전 비밀번호와 동일");

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
