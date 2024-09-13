package Fridge_Chef.team.email.rest;

import Fridge_Chef.team.cert.service.CertService;
import Fridge_Chef.team.cert.service.request.EmailVerifyRequest;
import Fridge_Chef.team.cert.service.request.SignUpCertRequest;
import Fridge_Chef.team.cert.service.request.SignUpEmailVerifyRequest;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.mail.rest.EmailSignupController;
import Fridge_Chef.team.mail.rest.request.SignUpEmailSendRequest;
import Fridge_Chef.team.mail.service.EmailService;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.request.UserEmailCheckRequest;
import Fridge_Chef.team.user.service.UserService;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static Fridge_Chef.team.exception.ErrorCode.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

@DisplayName("메일")
@WebMvcTest(EmailSignupController.class)
public class EmailSignupControllerTest extends RestDocControllerTests {

    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private UserService userService;
    @MockBean
    private CertService certService;
    @MockBean
    private EmailService emailService;

    private User user;

    @BeforeEach
    void setup() {
        String email = "UserFixture_1_@gmail.com";
        String pw = "password";
        user = UserFixture.create(email, pw);
    }

    @Test
    void email_success() throws Exception {
        UserEmailCheckRequest jsonRequest = new UserEmailCheckRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);

        doNothing().when(userService).checkEmailValidAndUnique(jsonRequest.email());

        ResultActions actions = jsonPostWhen("/api/email/check", request);

        actions.andExpect(status().isOk())
                .andDo(document("이메일 중복체크",
                        userEmailCheckRequestProvider()
                ))
        ;
    }

    @Test
    void email_check_fail() throws Exception {
        UserEmailCheckRequest jsonRequest = new UserEmailCheckRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_EMAIL_UNIQUE;

        doThrow(new ApiException(USER_EMAIL_UNIQUE))
                .when(userService).checkEmailValidAndUnique(jsonRequest.email());

        ResultActions actions = jsonPostWhen("/api/email/check", request);

        failResultAction(actions, "이메일 중복체크", userEmailCheckRequestProvider(), errorCode);
    }


    @Test
    void cert_email_send_success() throws Exception {
        SignUpEmailSendRequest jsonRequest = new SignUpEmailSendRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);

        doNothing().when(userService).checkEmailValidAndUnique(jsonRequest.email());
        when(certService.newVerificationCode()).thenReturn(123456);
        doNothing().when(emailService).signupCertSend(jsonRequest.email(), 123456);
        doNothing().when(certService).saveCert(new SignUpCertRequest(123456, jsonRequest.email()));

        ResultActions actions = jsonPostWhen("/api/email/send", request);

        actions.andExpect(status().isOk())
                .andDo(document("이메일 인증전송",
                        userEmailSendRequestProvider()
                ));

    }

    @Test
    void cert_email_send_fail() throws Exception {
        SignUpEmailSendRequest jsonRequest = new SignUpEmailSendRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = USER_EMAIL_UNIQUE;

        doThrow(new ApiException(errorCode)).when(userService).checkEmailValidAndUnique(jsonRequest.email());
        when(certService.newVerificationCode()).thenReturn(123456);
        doNothing().when(emailService).signupCertSend(jsonRequest.email(), 123456);
        doNothing().when(certService).saveCert(new SignUpCertRequest(123456, jsonRequest.email()));

        ResultActions actions = jsonPostWhen("/api/email/send", request);

        failResultAction(actions, "이메일 인증전송", userEmailSendRequestProvider(), errorCode);
    }

    @Test
    void cert_email_send_fail_parse() throws Exception {
        SignUpEmailSendRequest jsonRequest = new SignUpEmailSendRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = EMAIL_SEND_PARSE; // EMAIL_SEND_AUTHENTICATION , EMAIL_SEND

        doNothing().when(userService).checkEmailValidAndUnique(jsonRequest.email());
        when(certService.newVerificationCode()).thenReturn(123456);
        doThrow(new ApiException(errorCode)).when(emailService).signupCertSend(jsonRequest.email(), 123456);

        ResultActions actions = jsonPostWhen("/api/email/send", request);

        failResultAction(actions, "이메일 인증전송", userEmailSendRequestProvider(), errorCode);
    }

    @Test
    void cert_email_send_fail_auth() throws Exception {
        SignUpEmailSendRequest jsonRequest = new SignUpEmailSendRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = EMAIL_SEND_AUTHENTICATION; // EMAIL_SEND_AUTHENTICATION , EMAIL_SEND

        doNothing().when(userService).checkEmailValidAndUnique(jsonRequest.email());
        when(certService.newVerificationCode()).thenReturn(123456);
        doThrow(new ApiException(errorCode)).when(emailService).signupCertSend(jsonRequest.email(), 123456);

        ResultActions actions = jsonPostWhen("/api/email/send", request);

        failResultAction(actions, "이메일 인증전송", userEmailSendRequestProvider(), errorCode);
    }

    @Test
    void cert_email_send_fail_mail() throws Exception {
        SignUpEmailSendRequest jsonRequest = new SignUpEmailSendRequest(user.getEmail());
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = EMAIL_SEND; // EMAIL_SEND_AUTHENTICATION , EMAIL_SEND

        doNothing().when(userService).checkEmailValidAndUnique(jsonRequest.email());
        when(certService.newVerificationCode()).thenReturn(123456);
        doThrow(new ApiException(errorCode)).when(emailService).signupCertSend(jsonRequest.email(), 123456);

        ResultActions actions = jsonPostWhen("/api/email/send", request);

        failResultAction(actions, "이메일 인증전송", userEmailSendRequestProvider(), errorCode);
    }


    @Test
    void cert_email_verify_success() throws Exception {
        SignUpEmailVerifyRequest jsonRequest = new SignUpEmailVerifyRequest(user.getEmail(), 123456);
        String request = objectMapper.writeValueAsString(jsonRequest);

        doNothing().when(certService).emailVerifyCodeCheck(EmailVerifyRequest.of(jsonRequest.email(), jsonRequest.code()));

        ResultActions actions = jsonPostWhen("/api/email/verify", request);

        actions.andExpect(status().isOk())
                .andDo(document("이메일 인증번호검증",
                        userEmailVerifyRequestProvider()
                ));
    }

    @Test
    void cert_email_verify_fail() throws Exception {
        SignUpEmailVerifyRequest jsonRequest = new SignUpEmailVerifyRequest(user.getEmail(), 123456);
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = SIGNUP_EMAIL_VERIFY_CODE_FAILED;

        doThrow(new ApiException(errorCode)).when(certService).emailVerifyCodeCheck(EmailVerifyRequest.of(jsonRequest.email(), jsonRequest.code()));

        ResultActions actions = jsonPostWhen("/api/email/verify", request);

        failResultAction(actions, "이메일 인증인증실패", userEmailVerifyRequestProvider(), errorCode);
    }

    private RequestFieldsSnippet userEmailVerifyRequestProvider() {
        return requestFields(
                fieldWithPath("email").description("이메일").optional(),
                fieldWithPath("code").type(JsonFieldType.NUMBER).description("인증 코드 (6자리)").optional()
        );
    }

    private RequestFieldsSnippet userEmailCheckRequestProvider() {
        return requestFields(
                fieldWithPath("email").description("이메일").optional()
        );
    }

    private RequestFieldsSnippet userEmailSendRequestProvider() {
        return requestFields(List.of(
                fieldWithPath("email").description("이메일").optional()
        ));
    }
}
