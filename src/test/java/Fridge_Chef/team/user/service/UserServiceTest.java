package Fridge_Chef.team.user.service;

import Fridge_Chef.team.common.ServiceLayerTest;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.user.domain.User;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest extends ServiceLayerTest {

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = UserFixture.create("test@gmail.com", "test");

    }

    @Test
    @DisplayName("회원 가입")
    void signup() {
        // Given
        String email = "email@gmail.com";
        String password = "qwer1234";
        String name = "1234qwer";
        User whenUser = User.create(email, password, name);

        // When
        when(passwordEncoder.encode(anyString())).thenReturn(password);
        when(userRepository.save(any(User.class))).thenReturn(whenUser);
        userService.signup(email, password, name);

        //then
        verify(userRepository).save(any(User.class));
    }


    @Test
    @DisplayName("회원 탈퇴 - 탈퇴 요청한 회원")
    void account_delete() {
        user.accountDelete(true);

        when(userRepository.findByUserId_Value(user.getId()))
                .thenReturn(Optional.of(user));

        assertThrows(ApiException.class, () -> {
            userService.accountDelete(user.getUserId(), user.getUsername());
        });
    }

    @Test
    @DisplayName("회원 탈퇴 - 입력값 검증")
    void account_delete_input_name() {
        String username = user.getUsername() + "_";

        when(userRepository.findByUserId_Value(user.getId()))
                .thenReturn(Optional.of(user));

        assertThrows(ApiException.class, () -> {
            userService.accountDelete(user.getUserId(), username);
        });
    }

    @Test
    @DisplayName("비번 변경 - 현재 입력값 검증")
    void update_password() {
        String password = user.getPassword();
        String newPassword = user.getPassword() + "_";

        when(userRepository.findByUserId_Value(user.getId()))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword()))
                .thenReturn(true);

        userService.updatePassword(user.getUserId(), password, newPassword);

        assertThrows(ApiException.class, () -> {
            userService.updatePassword(user.getUserId(), password, password);
        });
    }

    @Test
    @DisplayName("로그인 검증 ")
    void login_success() {
        when(passwordEncoder.matches(user.getPassword(), user.getPassword()))
                .thenReturn(true);

        assertDoesNotThrow(() -> userService.authenticate(user, user.getPassword()));
    }

    @Test
    @DisplayName("로그인 검증 - 비밀번호 불일치로 인한 로그인 실패 ")
    void login_password_fail() {
        String wrongPassword = "wrong_password";

        when(passwordEncoder.matches(wrongPassword, user.getPassword()))
                .thenReturn(false);

        assertThrows(ApiException.class, () -> {
            userService.authenticate(user, wrongPassword);
        });
    }
}
