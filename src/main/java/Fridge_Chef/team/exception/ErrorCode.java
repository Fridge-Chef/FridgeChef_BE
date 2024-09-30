package Fridge_Chef.team.exception;


import java.util.Arrays;

/**
 * 에러 코드
 */
public enum ErrorCode {
    //Server
    INTERNAL_SERVER_ERROR(500, "서버 오류"),
    INVALID_VALUE(400, "유효하지 않은 값 입니다."),
    FILED_UNIQUE(499, "중복된 값 입니다."),

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

    //email
    EMAIL_SEND_PARSE(405, "메시지 구문 분석에 실패"),
    EMAIL_SEND_AUTHENTICATION(406, "인증이 실패"),
    EMAIL_SEND(407, "메시지 전송에 실패"),


    //signup
    USER_ACCOUNT_DELETE(408, "탈퇴 요청한 사용자 입니다. "),
    USER_ACCOUNT_DELETE_NAME_INCORRECT(409, "회원탈퇴 요청중 이름을 다르게 입력"),

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
    BOARD_NOT_USER_CREATE(406, "내가 생성하지 않는 게시판"),
    BOARD_NOT_FOUND(407, "찾을 수 없는 게시물 입니다."),
    STAR_RATING_IS_1_0_OR_HIGHER_AND_5_0_OR_LOWER(407, "별점은 1~ 5점 사이만 가능"),
    RATING_IS_0_5_UNITS(408, "별점은 0.5단위만 가능"),
    COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없음"),
    COMMENT_NOT_USER_AUTHOR(406, "댓글 작성자가 아님"),
    COMMENT_NOT_BOARD(407, "댓글의 게시글이 아님"),
    SIGNUP_USER_FAIL_SNS_EMAIL_UNIQUE(405,"회원가입 SNS 가입 중복 " ),
    SIGNUP_SNS_NOT_SUPPORT(411, "지원하지 않는 SNS 인증 타입");

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
