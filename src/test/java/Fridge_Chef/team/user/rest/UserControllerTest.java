package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.cert.service.CertService;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.request.UserAuthenticateRequest;
import Fridge_Chef.team.user.service.UserService;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static Fridge_Chef.team.exception.ErrorCode.LOGIN_PASSWORD_INCORRECT;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(UserController.class)
public class UserControllerTest extends RestDocControllerTests {
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private UserService userService;
    @MockBean
    private CertService certService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setup() {
        String email = "UserFixture_1_@gmail.com";
        String pw = "password";
        user = UserFixture.create(email, pw);
    }

    @Test
    void login_success() throws Exception {
        UserAuthenticateRequest userAuthenticateRequest = new UserAuthenticateRequest(user.getEmail(), user.getPassword());
        String request = objectMapper.writeValueAsString(userAuthenticateRequest);

        when(userService.findUserByEmail(userAuthenticateRequest.email()))
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
        UserAuthenticateRequest userAuthenticateRequest = new UserAuthenticateRequest(user.getEmail(), "wrongPassword");
        String request = objectMapper.writeValueAsString(userAuthenticateRequest);

        when(userService.findUserByEmail(userAuthenticateRequest.email()))
                .thenReturn(user);

        doThrow(new ApiException(LOGIN_PASSWORD_INCORRECT))
                .when(userService).authenticate(user, "wrongPassword");

        ResultActions actions = jsonPostWhen("/api/user/login", request);

        actions.andExpect(status(LOGIN_PASSWORD_INCORRECT))
                .andDo(document("로그인 실패 - 비밀번호 불일치",
                        userLoginRequestProvider(),
                        errorFields(LOGIN_PASSWORD_INCORRECT)
                ));
    }

    public RequestFieldsSnippet userLoginRequestProvider() {
        return requestFields(Arrays.asList(
                fieldWithPath("email").description("이메일"),
                fieldWithPath("password").description("비밀번호: 6~38자의 영문, 숫자를 사용해주세요.")
        ));
    }

}
