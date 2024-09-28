package Fridge_Chef.team.comment.domain;

import Fridge_Chef.team.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("댓글")
public class CommentTest {
    @ParameterizedTest
    @MethodSource("starSuccessProvider")
    @DisplayName("별점 변경 - 성공 ")
    void star_success(double star){
        Comment comment = new Comment();
        assertDoesNotThrow(() -> comment.updateStar(star));
    }

    @ParameterizedTest
    @MethodSource("starFailProvider")
    @DisplayName("별점 변경 - 실패 검증 ")
    void star_fail(double star){
        Comment comment = new Comment();
        assertThrows(ApiException.class, () -> comment.updateStar(star));
    }

    private static Stream<Double> starSuccessProvider() {
        return Stream.of(1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0);
    }

    private static Stream<Double> starFailProvider() {
        return Stream.of(0.5, 0.0, 5.5, 6.0, 4.7, 3.3, -1.0);
    }
}
