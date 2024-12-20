package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.common.docs.CustomPart;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.security.rest.OauthController;
import Fridge_Chef.team.security.rest.request.MobileLoginRequest;
import Fridge_Chef.team.security.service.CustomOAuth2UserService;
import Fridge_Chef.team.security.service.factory.provider.CustomOAuth2ClientProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.request.UserAccountDeleteRequest;
import Fridge_Chef.team.user.rest.request.UserProfileNameUpdateRequest;
import Fridge_Chef.team.user.rest.response.UserProfileMyPageResponse;
import Fridge_Chef.team.user.rest.response.UserProfileResponse;
import Fridge_Chef.team.user.service.UserService;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static Fridge_Chef.team.exception.ErrorCode.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static fixture.ImageFixture.partImage;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@DisplayName("유저")
@WebMvcTest({UserController.class, OauthController.class})
public class UserControllerTest extends RestDocControllerTests {
    @MockBean
    private UserService userService;
    @MockBean
    private ImageLocalService imageService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtProvider jwtProvider;
    @MockBean
    private CustomOAuth2UserService oAuth2UserService;
    @MockBean
    private CustomOAuth2ClientProvider customOAuth2ClientProvider;
    private User user;

    @BeforeEach
    void setup() {
        String email = "UserFixture_1_@gmail.com";
        user = UserFixture.create(email);
    }

    @Test
    void moblie_login_success() throws Exception {
        MobileLoginRequest jsonRequest = new MobileLoginRequest("token", "kakao");
        String request = objectMapper.writeValueAsString(jsonRequest);

        when(customOAuth2ClientProvider.getClientProperties(anyString()))
                .thenReturn(clientRegistration());
        when(oAuth2UserService.loadMoblieToUser(any()))
                .thenReturn(user);
        when(jwtProvider.create(user.getUserId(), user.getRole()))
                .thenReturn("token");

        jsonPostWhen("/api/mobile/auth/login", request)
                .andExpect(status().isOk())
                .andDo(document("모바일 로그인",
                        requestFields(
                                fieldWithPath("token").description("모바일 토큰"),
                                fieldWithPath("registration").description("등록된 소셜 로그인")
                        ),
                        responseFields(
                                fieldWithPath("user").description("유저 정보"),
                                fieldWithPath("user.email").description("이메일"),
                                fieldWithPath("user.token").description("토큰"),
                                fieldWithPath("user.username").description("이름")
                        )
                ));
    }

    @Test
    void moblie_login_fail_token_access_expired_fail() throws Exception {
        MobileLoginRequest jsonRequest = new MobileLoginRequest("token", "kakao");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = TOKEN_ACCESS_EXPIRED_FAIL;

        when(customOAuth2ClientProvider.getClientProperties(anyString()))
                .thenReturn(clientRegistration());
        when(oAuth2UserService.loadMoblieToUser(any()))
                .thenThrow(new ApiException(errorCode));

        failResultAction(jsonPostWhen("/api/mobile/auth/login", request), "회원탈퇴 ", userAccountRequestProvider(), errorCode);
    }

    @Test
    void moblie_login_fail_signup_user_fail_sns_email_unique() throws Exception {
        MobileLoginRequest jsonRequest = new MobileLoginRequest("token", "kakao");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = SIGNUP_USER_FAIL_SNS_EMAIL_UNIQUE;

        when(customOAuth2ClientProvider.getClientProperties(anyString()))
                .thenReturn(clientRegistration());
        when(oAuth2UserService.loadMoblieToUser(any()))
                .thenThrow(new ApiException(errorCode));

        failResultAction(jsonPostWhen("/api/mobile/auth/login", request), "회원탈퇴 ", userAccountRequestProvider(), errorCode);
    }

    @Test
    void moblie_login_fail_not_support() throws Exception {
        MobileLoginRequest jsonRequest = new MobileLoginRequest("token", "kakao");
        String request = objectMapper.writeValueAsString(jsonRequest);
        ErrorCode errorCode = SIGNUP_SNS_NOT_SUPPORT;

        when(customOAuth2ClientProvider.getClientProperties(anyString()))
                .thenReturn(clientRegistration());
        when(oAuth2UserService.loadMoblieToUser(any()))
                .thenThrow(new ApiException(errorCode));

        failResultAction(jsonPostWhen("/api/mobile/auth/login", request), "회원탈퇴 ", userAccountRequestProvider(), errorCode);
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

    @Test
    @WithMockCustomUser
    void mypage_select() throws Exception {
        when(userService.findByMyPage(any(UserId.class)))
                .thenReturn(new UserProfileMyPageResponse(1, 5));

        ResultActions actions = jwtGetWhen("/api/user/mypage");

        actions.andExpect(status().isOk())
                .andDo(document("마이페이지 조회",
                        jwtTokenRequest(),
                        responseFields(
                                fieldWithPath("recipeCount").description("레시피 카운트").type(JsonFieldType.NUMBER),
                                fieldWithPath("commentCount").description("댓글 카운트").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    void profile_image_update() throws Exception {
        when(imageService.imageUpload(any(UserId.class), any()))
                .thenReturn(Image.outUri("url"));
        CustomPart part = partImage("image", "이미지 파일", true);

        var actions = mockMvc.perform(jwtFormPatchWhen("/api/user/picture", part));

        actions.andExpect(status().isOk())
                .andDo(document("유저 이미지 수정",
                        jwtTokenRequest(),
                        requestPartsForm(List.of(part))
                ));
    }

    @Test
    @WithMockCustomUser
    void profile_image_update_fail_image_remote_upload() throws Exception {
        when(imageService.imageUpload(any(UserId.class), any()))
                .thenThrow(new ApiException(IMAGE_REMOTE_UPLOAD));
        CustomPart part = partImage("image", "이미지 파일", true);

        var actions = mockMvc.perform(jwtFormPatchWhen("/api/user/picture", part));


        failJwtResultAction(actions, "유저 이미지 수정", requestPartsForm(List.of(part)), IMAGE_REMOTE_UPLOAD);
    }

    @Test
    @WithMockCustomUser
    void profile_image_update_fail_image_file_analysis() throws Exception {
        when(imageService.imageUpload(any(UserId.class), any()))
                .thenThrow(new ApiException(IMAGE_FILE_ANALYIS));
        CustomPart part = partImage("image", "이미지 파일", true);

        var actions = mockMvc.perform(jwtFormPatchWhen("/api/user/picture", part));

        failJwtResultAction(actions, "유저 이미지 수정", requestPartsForm(List.of(part)), IMAGE_FILE_ANALYIS);
    }

    private RequestFieldsSnippet userAccountDeleteRequestProvider() {
        return requestFields(List.of(
                fieldWithPath("username").description("탈퇴 여부 재확인용 이름 입력")
        ));
    }

    private RequestFieldsSnippet userAccountRequestProvider() {
        return requestFields(List.of(
                fieldWithPath("token").description("모바일 토큰"),
                fieldWithPath("registration").description("등록된 소셜 로그인")
        ));
    }

    private ClientRegistration clientRegistration() {
        return ClientRegistration.withRegistrationId("kakao")
                .clientId("clientId")
                .clientSecret("client")
                .clientName("kakao")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .scope("profile")
                .authorizationUri("https://kauth.kakao.com/oauth/authorize")
                .tokenUri("https://kauth.kakao.com/oauth/token")
                .userInfoUri("https://kapi.kakao.com/v2/user/me")
                .userNameAttributeName("id")
                .jwkSetUri("https://kauth.kakao.com/oauth/token")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .clientName("kakao")
                .build();
    }
}
