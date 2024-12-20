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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class UserServiceTest extends ServiceLayerTest {

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        user = UserFixture.create("test@gmail.com");
    }

    @Test
    @DisplayName("회원 탈퇴 - 탈퇴 요청한 회원")
    void account_delete() {
        user.accountDelete(true);

        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(Optional.of(user));

        assertThrows(ApiException.class, () -> {
            userService.accountDelete(user.getUserId(), user.getUsername());
        });
    }

    @Test
    @DisplayName("회원 탈퇴 - 입력값 검증")
    void account_delete_input_name() {
        String username = user.getUsername() + "_";

        when(userRepository.findByUserId(user.getUserId()))
                .thenReturn(Optional.of(user));

        assertThrows(ApiException.class, () -> {
            userService.accountDelete(user.getUserId(), username);
        });
    }
}
