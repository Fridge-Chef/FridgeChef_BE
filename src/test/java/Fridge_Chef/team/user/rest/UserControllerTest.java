package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.request.UserAccountDeleteRequest;
import Fridge_Chef.team.user.rest.request.UserProfileNameUpdateRequest;
import Fridge_Chef.team.user.rest.response.UserProfileResponse;
import Fridge_Chef.team.user.service.UserService;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

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
    private UserRepository userRepository;
    @MockBean
    private ImageService imageService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    private User user;
    @Autowired
    private JwtProvider jreJwtProvider;

    @BeforeEach
    void setup() {
        String email = "UserFixture_1_@gmail.com";
        user = UserFixture.create(email);
    }

    @Test
    void login_success() {
        String email = "kakao@gmail.com";
        UserFixture.create(email);
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
    void user_select() throws Exception {
        when(userService.findBysUserProfile(any(UserId.class)))
                .thenReturn(UserProfileResponse.from(user));

        ResultActions actions = jwtGetWhen("/api/user");

        actions.andExpect(status().isOk())
                .andDo(document("유저 조회",
                        jwtTokenRequest(),
                        responseFields(
                                fieldWithPath("user").description("유저").type(JsonFieldType.OBJECT),
                                fieldWithPath("user.email").description("이메일"),
                                fieldWithPath("user.role").description("권한"),
                                fieldWithPath("user.username").description("이름"),
                                fieldWithPath("user.profileLink").description("프로필 주소"),
                                fieldWithPath("user.createAt").description("생성 날짜")
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    void profile_name_update_success() throws Exception {
        UserProfileNameUpdateRequest jsonRequest = new UserProfileNameUpdateRequest("newUsername");
        String request = objectMapper.writeValueAsString(jsonRequest);

        ResultActions actions = jwtJsonPatchWhen("/api/user/name", request);

        actions.andExpect(status().isOk())
                .andDo(document("유저 이름 수정",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("username").description("변경할 유저 이름")
                        )
                ));
    }

    private RequestFieldsSnippet userAccountDeleteRequestProvider() {
        return requestFields(List.of(
                fieldWithPath("username").description("탈퇴 여부 재확인용 이름 입력")
        ));
    }
}
