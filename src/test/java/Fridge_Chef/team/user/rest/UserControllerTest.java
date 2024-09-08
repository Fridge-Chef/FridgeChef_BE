package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.request.UserAuthenticateRequest;
import Fridge_Chef.team.user.service.UserService;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@WebMvcTest(UserController.class)
public class UserControllerTest extends RestDocControllerTests {

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setup() {
        String email = "UserFixture_1_@gmail.com";
        String pw = "password";
        user = UserFixture.create(email, passwordEncoder.encode(pw));
    }

    @Test
    void testInfo() throws Exception {
        UserAuthenticateRequest userAuthenticateRequest = new UserAuthenticateRequest(user.getEmail(), user.getPassword());
        String request = objectMapper.writeValueAsString(userAuthenticateRequest);

        when(userService.findUserByEmail(userAuthenticateRequest.email()))
                .thenReturn(user);

        when(jwtProvider.create(user.getUserId()))
                .thenReturn("dummy-jwt-token");

        ResultActions actions = jsonPostWhen("/api/user/login", request);

        actions.andExpect(status().isOk())
                .andDo(document("로그인",
                        requestFields(
                                fieldWithPath("email").description("The email of the user"),
                                fieldWithPath("password").description("The password of the user")
                        ),
                        responseFields(
                                fieldWithPath("user").description("The user object"),
                                fieldWithPath("user.email").description("The email of the user"),
                                fieldWithPath("user.token").description("JWT token"),
                                fieldWithPath("user.username").description("The username of the user")
                        )
                ));
    }
}
