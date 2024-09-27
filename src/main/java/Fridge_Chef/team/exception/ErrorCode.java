package Fridge_Chef.team.exception;


import java.util.Arrays;

/**
 * 에러 코드
 */
public enum ErrorCode {
    //Server
    INTERNAL_SERVER_ERROR(500, "서버 오류"),
    INVALID_VALUE(400, "유효하지 않은 값 입니다."),
    FILED_UNIQUE(499,"중복된 값 입니다." ),

    //security
    SECURITY_AUTHENTICATION_METHOD_NOT_SUPPORTED(450, "인증 방법이 지원되지 않습니다."),
    SECURITY_MESSAGE(451, ""),

    //token
    TOKEN_ACCESS_EXPIRED_FAIL(401, "토큰값 만료"),
    TOKEN_ACCESS_NOT_USER(400, "토큰값 유저정보 없음"),

    //image
    IMAGE_REMOTE_UPLOAD(405, "이미지 원격 업로드 실패"),
    IMAGE_REMOTE_SESSION(406, "이미지 세션 오류"),
    IMAGE_REMOTE_DELETE_FAIL(407, "이미지 삭제 실패"),
    IMAGE_NOT_ID(408, "이미지의 고유한 ID 가 존재하지 않음"),
    IMAGE_AUTHOR_MISMATCH(409, "이미지의 작성자가 일치하지 않음"),

    //USER
    USER_EMAIL_UNIQUE(406, "이메일 중복 에러"),
    USER_NOT_FOUND(405, "유저 정보 없음"),
    USER_NOT_EMAIL(407, "이메일 정보 없음"),
    LOGIN_PASSWORD_INCORRECT(408, "로그인 패스워드 불일치"),

    //email
    EMAIL_SEND_PARSE(405, "메시지 구문 분석에 실패"),
    EMAIL_SEND_AUTHENTICATION(406, "인증이 실패"),
    EMAIL_SEND(407, "메시지 전송에 실패"),


    //signup
    SIGNUP_EMAIL_VERIFY_CODE_FAILED(405, "이메일 인증 실패"),
    SIGNUP_EMAIL_DUPLICATE(406, "회원가입 이메일 중복"),
    SIGNUP_CERT_CODE_UNVERIFIED(407, "회원가입 인증코드 미인증"),
    USER_ACCOUNT_DELETE(408, "탈퇴 요청한 사용자 입니다. "),
    USER_ACCOUNT_DELETE_NAME_INCORRECT(409, "회원탈퇴 요청중 이름을 다르게 입력"),
    USER_PASSWORD_INPUT_FAIL(410, "기존 비밀번호 틀림"),
    USER_NEW_PASSWORD_SAME_AS_OLD(411, "사용자 새 비밀번호가 이전 비밀번호와 동일"),

    PASSWORD_IS_REQUIRED(412, "비밀번호를 입력해주세요."),
    PASSWORD_VALID_FAIL(413, "비밀번호는 6자 이상 38자 이하의 숫자 또는 영문자로 입력해주세요."),
    SIGNUP_CERT_NON_REQUEST(414, "회원가입 인증과정을 무시했습니다."),

    //fridge
    FRIDGE_NOT_FOUND(404, "냉장고 정보 없음"),

    //ingredient
    INGREDIENT_NOT_FOUND(404, "재료 정보 없음"),

    IMAGE_FILE_ANALYIS(405, "이미지 파일 읽기 오류"),
    IMAGE_CONTENT_TYPE_FAIL(406, "지원하지 않는 이미지 타입(허용타입: png,jpeg,jpg"),
    IMAGE_FILE_DELETE_FAIL(407, "이미지 삭제 실패"),
    IMAGE_REMOVE_NOT_USER(408, "이미지 제거 유저 불일치"),
    CATEGORY_NOT_FOUND(405, "카테고리를 찾지 못했습니다."),
    CATEGORY_ALREADY(405, "카테고리가 이미 있습니다."),
    BOARD_NOT_USER_CREATE(406,"내가 생성하지 않는 게시판" ),
    BOARD_NOT_FOUND(407,"찾을 수 없는 게시물 입니다." ),
    STAR_RATING_IS_1_0_OR_HIGHER_AND_5_0_OR_LOWER(407,"별점은 1~ 5점 사이만 가능" ),
    RATING_IS_0_5_UNITS(408, "별점은 0.5단위만 가능");

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
