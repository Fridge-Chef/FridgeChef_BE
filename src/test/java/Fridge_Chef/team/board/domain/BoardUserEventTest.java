package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.user.domain.User;
import fixture.BoardFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("게시판 유저 이벤트 도메인 테스트")
public class BoardUserEventTest {
    private User user;
    private Board board;
    private BoardUserEvent event;

    @BeforeEach
    void setup() {
        user = UserFixture.create("test@Test.com");
        board = BoardFixture.create(user);
        event = new BoardUserEvent(board, user);
    }

    @Test
    @DisplayName(" Hit 증가시 0 , 1 값만 가지는가 ")
    void upTest() {
        event.hitUp();
        assertThat(event.getHit()).isEqualTo(1);
        event.hitUp();
        assertThat(event.getHit()).isEqualTo(0);
        event.hitUp();
        assertThat(event.getHit()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("starSuccessProvider")
    @DisplayName(" 별점 수정시 0.5 단위 , 1 ~ 5 값 사이 만 이동 ")
    void updateStarSuccess(double star) {
        assertDoesNotThrow(() -> event.updateStar(star));
    }

    @ParameterizedTest
    @MethodSource("starFailProvider")
    @DisplayName(" 별점 수정시 0.5 단위 , 1 ~ 5 값 범위 를 벗어남  ")
    void updateStarFail(double star) {
        assertThrows(ApiException.class, () -> event.updateStar(star));
    }

    @Test
    @DisplayName("내가 좋아요 성공")
    void isMyEventUserSuccess(){
        event.hitUp();
        assertThat(event.isUserHit(user.getUserId())).isEqualTo(true);
    }

    @Test
    @DisplayName("다른 유저 좋아요 비교 검증 성공")
    void isMyEventUserFail(){
        event.hitUp();
        assertThat(event.isUserHit(UserFixture.create("test@test.comm").getUserId())).isEqualTo(false);
    }

    @Test
    @DisplayName("좋아요 확인 true")
    void isUserHitSuccess(){
        event.hitUp();
        assertThat(event.isUserHit()).isEqualTo(true);
    }

    @Test
    @DisplayName("좋아요 확인 false")
    void isUserHitFail(){
        assertThat(event.isUserHit()).isEqualTo(false);
    }

    private static Stream<Double> starSuccessProvider() {
        return Stream.of(1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0);
    }

    private static Stream<Double> starFailProvider() {
        return Stream.of(0.5, 0.0, 5.5, 6.0, 4.7, 3.3, -1.0);
    }
}
