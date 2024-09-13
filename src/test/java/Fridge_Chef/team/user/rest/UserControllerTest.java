package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.cert.service.CertService;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.mail.rest.request.SignUpRequest;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.request.UserAccountDeleteRequest;
import Fridge_Chef.team.user.rest.request.UserAuthenticateRequest;
import Fridge_Chef.team.user.rest.request.UserPasswordChangeRequest;
import Fridge_Chef.team.user.service.UserService;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static Fridge_Chef.team.exception.ErrorCode.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@DisplayName("유저")
@WebMvcTest(UserController.class)
public class UserControllerTest extends RestDocControllerTests {
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private UserService userService;
    @MockBean
    private CertService certService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    private User user;
    @Autowired
    private JwtProvider jreJwtProvider;

    @BeforeEach
    void setup() {
        String email = "UserFixture_1_@gmail.com";
        String pw = "password";
        user = UserFixture.create(email, pw);
    }

    @Test
    void login_success() throws Exception {
        UserAuthenticateRequest jsonRequest = new UserAuthenticateRequest(user.getEmail(), user.getPassword());
        String request = objectMapper.writeValueAsString(jsonRequest);

        when(userService.findUserByEmail(jsonRequest.email()))
                .thenReturn(user);
        when(jwtProvider.create(user.getUserId(), Role.USER))
                .thenReturn("jwt-token");

        ResultActions actions = jsonPostWhen("/api/user/login", request);

        actions.andExpect(status().isOk())
                .andDo(document("로그인",
                        userLoginRequestProvider(),
                        responseFields(
                                fieldWithPath("user").description("Object"),
                                fieldWithPath("user.email").description("이메일"),
                                fieldWithPath("user.token").description("JWT token"),
                                fieldWithPath("user.username").description("이름")
                        )
                ));
    }

    @Test
    void loginFailureDueToWrongPassword() throws Exception {
        UserAuthenticateRequest jsonRequest = new UserAuthenticateRequest(user.getEmail(), "wrongPassword");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = LOGIN_PASSWORD_INCORRECT;

        when(userService.findUserByEmail(jsonRequest.email()))
                .thenReturn(user);
        doThrow(new ApiException(LOGIN_PASSWORD_INCORRECT))
                .when(userService).authenticate(user, "wrongPassword");

        ResultActions actions = jsonPostWhen("/api/user/login", request);

        failResultAction(actions, "로그인 실패", userLoginRequestProvider(), errorCode);
    }

    @Test
    void loginFailure_email_notfound() throws Exception {
        UserAuthenticateRequest jsonRequest = new UserAuthenticateRequest(user.getEmail(), "wrongPassword");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_NOT_EMAIL;

        doThrow(new ApiException(errorCode))
                .when(userService).findUserByEmail(user.getEmail());

        ResultActions actions = jsonPostWhen("/api/user/login", request);

        failResultAction(actions, "로그인 실패", userLoginRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void account_delete() throws Exception {
        UserAccountDeleteRequest jsonRequest = new UserAccountDeleteRequest(user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);

        doNothing().when(userService).accountDelete(user.getUserId(), jsonRequest.username());

        ResultActions actions = jwtJsonDeleteWhen("/api/user/account", request);

        actions.andExpect(status().isOk())
                .andDo(document("회원탈퇴",
                        userAccountDeleteRequestProvider()
                ));
    }

    @Test
    @WithMockCustomUser
    void account_delete_fail_not_user() throws Exception {
        UserAccountDeleteRequest jsonRequest = new UserAccountDeleteRequest(user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_NOT_FOUND;

        doThrow(new ApiException(errorCode))
                .when(userService)
                .accountDelete(any(UserId.class), eq(jsonRequest.username()));

        ResultActions actions = jwtJsonDeleteWhen("/api/user/account", request);

        failResultAction(actions, "회원탈퇴 ", userAccountDeleteRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void account_delete_fail_not_found_user() throws Exception {
        UserAccountDeleteRequest jsonRequest = new UserAccountDeleteRequest(user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_ACCOUNT_DELETE;

        doThrow(new ApiException(errorCode))
                .when(userService)
                .accountDelete(any(UserId.class), eq(jsonRequest.username()));

        ResultActions actions = jwtJsonDeleteWhen("/api/user/account", request);

        failResultAction(actions, "회원탈퇴 ", userAccountDeleteRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void account_delete_fail_input_name_incorrect() throws Exception {
        UserAccountDeleteRequest jsonRequest = new UserAccountDeleteRequest(user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_ACCOUNT_DELETE_NAME_INCORRECT;

        doThrow(new ApiException(errorCode))
                .when(userService)
                .accountDelete(any(UserId.class), eq(jsonRequest.username()));

        ResultActions actions = jwtJsonDeleteWhen("/api/user/account", request);

        failResultAction(actions, "회원탈퇴 ", userAccountDeleteRequestProvider(), errorCode);
    }


    @Test
    @WithMockCustomUser
    void password_change_success() throws Exception {
        UserPasswordChangeRequest jsonRequest = new UserPasswordChangeRequest("password", "newPassword");
        String request = objectMapper.writeValueAsString(jsonRequest);

        doNothing().when(userService).updatePassword(user.getUserId(), jsonRequest.password(), jsonRequest.newPassword());

        ResultActions actions = jwtJsonPatchWhen("/api/user/password", request);

        actions.andExpect(status().isOk())
                .andDo(document("비밀번호 변경",
                        jwtTokenRequest(),
                        userPasswordChangeRequestProvider()
                ));
    }

    @Test
    @WithMockCustomUser
    void password_change_user_not_found() throws Exception {
        UserPasswordChangeRequest jsonRequest = new UserPasswordChangeRequest("password", "newPassword");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_NOT_FOUND;

        doThrow(new ApiException(errorCode))
                .when(userService)
                .updatePassword(any(UserId.class), eq(jsonRequest.password()), eq(jsonRequest.newPassword()));

        ResultActions actions = jwtJsonPatchWhen("/api/user/password", request);

        failResultAction(actions, "비밀번호 변경 ", userPasswordChangeRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void password_change_input_fail() throws Exception {
        UserPasswordChangeRequest jsonRequest = new UserPasswordChangeRequest("Aassword", "newPassword");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_PASSWORD_INPUT_FAIL;

        doThrow(new ApiException(errorCode))
                .when(userService)
                .updatePassword(any(UserId.class), eq(jsonRequest.password()), eq(jsonRequest.newPassword()));

        ResultActions actions = jwtJsonPatchWhen("/api/user/password", request);

        failResultAction(actions, "비밀번호 변경 ", userPasswordChangeRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void password_change_same_as_old() throws Exception {
        UserPasswordChangeRequest jsonRequest = new UserPasswordChangeRequest("password", "password");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_NEW_PASSWORD_SAME_AS_OLD;

        doThrow(new ApiException(errorCode))
                .when(userService)
                .updatePassword(any(UserId.class), eq(jsonRequest.password()), eq(jsonRequest.newPassword()));

        ResultActions actions = jwtJsonPatchWhen("/api/user/password", request);

        failResultAction(actions, "비밀번호 변경 ", userPasswordChangeRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void password_change_valid_fail() throws Exception {
        UserPasswordChangeRequest jsonRequest = new UserPasswordChangeRequest("pas", "npa");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = PASSWORD_VALID_FAIL;

        doThrow(new ApiException(errorCode)).when(userService)
                .updatePassword(any(UserId.class), eq(jsonRequest.password()), eq(jsonRequest.newPassword()));

        ResultActions actions = jwtJsonPatchWhen("/api/user/password", request);

        failResultAction(actions, "비밀번호 변경 ", userPasswordChangeRequestProvider(), errorCode);
    }

    @Test
    @WithMockCustomUser
    void user_select() throws Exception {
        when(userService.findByUser(any(UserId.class)))
                .thenReturn(user);

        ResultActions actions = jwtGetWhen("/api/user");

        actions.andExpect(status().isOk())
                .andDo(document("유저 조회",
                        jwtTokenRequest(),
                        responseFields(
                                fieldWithPath("user").description("유저"),
                                fieldWithPath("user.email").description("이메일"),
                                fieldWithPath("user.role").description("권한"),
                                fieldWithPath("user.username").description("이름"),
                                fieldWithPath("user.createAt").description("생성날짜")
                        )
                ));
    }

    @Test
    void signup_success() throws Exception {
        SignUpRequest jsonRequest = new SignUpRequest(user.getEmail(),
                user.getPassword(), user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);

        doNothing().when(certService).validateCert(jsonRequest.email());
        doNothing().when(userService).validateMemberRegistration(jsonRequest.email());
        when(userService.signup(jsonRequest.email(), jsonRequest.password(), jsonRequest.username())).
                thenReturn(user);
        doNothing().when(certService).deleteAuthenticationComplete(any(SignUpRequest.class));

        ResultActions actions = jsonPostWhen("/api/user/signup", request);

        actions.andExpect(status().isOk())
                .andDo(document("회원가입",
                        userSignupRequestProvider(),
                        userSignupResponseProvider()
                ));
    }

    @Test
    void signup_fail_cert_non_request() throws Exception {
        SignUpRequest jsonRequest = new SignUpRequest(user.getEmail(),
                user.getPassword(), user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = SIGNUP_CERT_NON_REQUEST;

        doThrow(new ApiException(errorCode)).when(certService).validateCert(jsonRequest.email());
        ResultActions actions = jsonPostWhen("/api/user/signup", request);

        failResultAction(actions, "회원가입 ", userSignupRequestProvider(), errorCode);
    }

    @Test
    void signup_fail_cert_not_auth() throws Exception {
        SignUpRequest jsonRequest = new SignUpRequest(user.getEmail(),
                user.getPassword(), user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = SIGNUP_CERT_CODE_UNVERIFIED;

        doThrow(new ApiException(errorCode)).when(certService).validateCert(jsonRequest.email());
        ResultActions actions = jsonPostWhen("/api/user/signup", request);

        failResultAction(actions, "회원가입 ", userSignupRequestProvider(), errorCode);
    }

    @Test
    void signup_fail_email_dup() throws Exception {
        SignUpRequest jsonRequest = new SignUpRequest(user.getEmail(),
                user.getPassword(), user.getProfile().getUsername());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = SIGNUP_EMAIL_DUPLICATE;

        doNothing().when(certService).validateCert(jsonRequest.email());
        doThrow(new ApiException(errorCode)).when(userService).validateMemberRegistration(jsonRequest.email());
        ResultActions actions = jsonPostWhen("/api/user/signup", request);

        failResultAction(actions, "회원가입 ", userSignupRequestProvider(), errorCode);
    }

    private RequestFieldsSnippet userSignupRequestProvider() {
        return requestFields(Arrays.asList(
                fieldWithPath("email").description("이메일 형식에 맞게 입력해주세요.").optional(),
                fieldWithPath("password").description("현재 " + PASSWORD_VALID_FAIL.getMessage()).optional(),
                fieldWithPath("username").description("이름:2~30자를 입력해 주세요.").optional()
        ));
    }

    public static ResponseFieldsSnippet userSignupResponseProvider() {
        return responseFields(Arrays.asList(
                fieldWithPath("user").description("유저"),
                fieldWithPath("user.email").description("이메일"),
                fieldWithPath("user.token").description("access token (30분)"),
                fieldWithPath("user.username").description("이름")
        ));
    }

    private RequestFieldsSnippet userPasswordChangeRequestProvider() {
        return requestFields(Arrays.asList(
                fieldWithPath("password").description("현재 비밀번호").optional(),
                fieldWithPath("newPassword").description("새로운 " + PASSWORD_VALID_FAIL.getMessage()).optional()
        ));
    }

    private RequestFieldsSnippet userAccountDeleteRequestProvider() {
        return requestFields(List.of(
                fieldWithPath("username").description("탈퇴 여부 재확인용 이름 입력").optional()
        ));
    }


    private RequestFieldsSnippet userLoginRequestProvider() {
        return requestFields(Arrays.asList(
                fieldWithPath("email").description("이메일").optional(),
                fieldWithPath("password").description(PASSWORD_VALID_FAIL.getMessage()).optional()
        ));
    }
}
