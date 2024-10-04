package Fridge_Chef.team.user.repository;

import Fridge_Chef.team.common.JpaTest;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.Social;
import Fridge_Chef.team.user.domain.User;
import fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("유저 repository")
public class UserRepositoryTest extends JpaTest {

    @Test
    @DisplayName("ID 조회 성공")
    void findByUserIdSuccess() {
        User saveUser = userRepository.save(UserFixture.create("test@gmail.com"));
        User user = userRepository.findByUserId(saveUser.getUserId()).get();

        assertThat(saveUser).isEqualTo(user);
    }

    @Test
    @DisplayName("ID 조회 실패 검증")
    void findByUserIdValidFail() {
        User saveUser = userRepository.save(UserFixture.create("test@gmail.com"));
        User failUser = userRepository.save(UserFixture.create("testtest@gmail.com"));
        User user = userRepository.findByUserId(failUser.getUserId()).get();

        assertThat(saveUser).isNotEqualTo(user);
    }


    @Test
    @DisplayName("이메일 중복 허용 인증 타입 구분 성공")
    void saveEmail() {
        User kakaoSignup = User.createSocialUser("userEmailCa@gmail.com", "test", Role.USER, Social.KAKAO);
        User googleSignup = User.createSocialUser("userEmailCa@gmail.com", "test", Role.USER, Social.GOOGLE);

        userRepository.save(kakaoSignup);
        userRepository.save(googleSignup);
    }
    @Test
    @DisplayName("이메일 , 인증 타입 복합 유니크 적용 검증")
    void saveEmailValid() {
        User kakaoSignup = User.createSocialUser("userEmailCa@gmail.com", "test", Role.USER, Social.KAKAO);
        User googleSignup = User.createSocialUser("userEmailCa@gmail.com", "test", Role.USER, Social.GOOGLE);
        User validSingup = User.createSocialUser("userEmailCa@gmail.com", "test", Role.USER, Social.GOOGLE);

        userRepository.save(kakaoSignup);
        userRepository.save(googleSignup);

        assertThatThrownBy(() -> {
            userRepository.save(validSingup);
            userRepository.count();
        });
    }
}
