package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.common.ServiceLayerTest;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.user.domain.User;
import fixture.BoardFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BoardRecipeServiceTest extends ServiceLayerTest {
    @InjectMocks
    private BoardRecipeService boardRecipeService;
    @Mock
    protected BoardIngredientService boardIngredientService;
    @Mock
    protected BoardRepository boardRepository;
    @Mock
    protected BoardUserEventRepository boardUserEventRepository;
    @Mock
    protected ImageService imageService;
    @Mock
    protected IngredientRepository ingredientRepository;
    @Mock
    protected RecipeIngredientRepository recipeIngredientRepository;
    private User user;
    private Board board;

    @BeforeEach
    void setup() {
        user = UserFixture.create("test@test.com");
        board = BoardFixture.create(user);
    }

    @Test
    @DisplayName("수정시 작성자가 만든 레시피가 아닌것을 검증")
    void validMyRecipeUpdate() {
        User newUser = UserFixture.create("test2@test.com");
        Board newBoard = BoardFixture.create(newUser);

        when(userRepository.findByUserId(any()))
                .thenReturn(Optional.of(newUser));
        when(boardRepository.findById(newBoard.getId()))
                .thenReturn(Optional.of(board));
        BoardByRecipeUpdateRequest request = new BoardByRecipeUpdateRequest(board.getId(), "update title", "update intro", null, 1L, false, "1분", "보통", "양념,",
                List.of(),
                List.of());

        assertThatThrownBy(() -> boardRecipeService.update(newUser.getUserId(), request))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("수정시 작성자가 만든 레시피가 맞는지 검증")
    void validMyRecipeUpdateEq() {
        when(userRepository.findByUserId(any()))
                .thenReturn(Optional.of(user));
        when(boardRepository.findById(board.getId()))
                .thenReturn(Optional.of(board));
        BoardByRecipeUpdateRequest request = new BoardByRecipeUpdateRequest(board.getId(), "update title", "update intro", null, 1L, false, "1분", "보통", "양념,",
                List.of(),
                List.of());
        Board result = boardRecipeService.update(user.getUserId(), request);
        assertEquals("update title", result.getTitle());
    }
}
