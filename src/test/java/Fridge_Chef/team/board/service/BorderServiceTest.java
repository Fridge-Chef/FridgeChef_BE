package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.repository.model.IssueType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import fixture.BoardFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static Fridge_Chef.team.common.UtilTest.executionTime;
import static Fridge_Chef.team.exception.ErrorCode.BOARD_NOT_USER_CREATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@DisplayName("나만의 게시판")
public class BorderServiceTest {
    private static final Random random = new Random();
    @Autowired
    private ImageLocalService imageService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private BoardUserEventRepository boardUserEventRepository;
    @Autowired
    private BoardIngredientService boardIngredientService;
    @Autowired
    private BoardRecipeService boardRecipeService;
    @Autowired
    private BoardService boardService;
    private User user;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserFixture.create("test@gmail.com"));
    }

    @ParameterizedTest
    @MethodSource("provideBoardCreateRequests")
    @DisplayName("추가")
    @Transactional
    void create(BoardByRecipeRequest request) {
        Image mainImage = imageService.imageUpload(user.getUserId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        boardRecipeService.create(user.getUserId(), request, ingredients, descriptions, mainImage);
    }


    @Test
    @DisplayName("단일 검색")
    @Transactional
    void find() {
        givenBoardContext();
        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
        BoardMyRecipeResponse response = boardService.findMyRecipeId(board.getId());
        assertAll(() -> board.getId().equals(response.getBoardId()),
                () -> board.getTitle().equals(response.getTitle()));
    }

    @Test
    @DisplayName("페이징")
    @Transactional
    void finds() {
        givenBoardContexts();
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(0, 20, IssueType.ALL, SortType.HIT);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(user.getUserId(), request);
            int left = result.getContent().get(0).getHit();
            int right = result.getContent().get(result.getSize() - 1).getHit();
            assertThat(left > right)
                    .withFailMessage("SortType.HIT: 좋아요순 정렬 실패")
                    .isTrue();
            return result;
        });
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(0, 20, IssueType.ALL, SortType.CLICKS);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(user.getUserId(), request);
            int left = result.getContent().get(0).getClick();
            int right = result.getContent().get(result.getSize() - 1).getClick();
            assertThat(left > right)
                    .withFailMessage("SortType.CLICKS: 클릭순 정렬 실패")
                    .isTrue();
            return result;
        });
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(0, 20, IssueType.ALL, SortType.LATEST);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(user.getUserId(), request);
            LocalDateTime left = result.getContent().get(0).getCreateTime();
            LocalDateTime right = result.getContent().get(result.getSize() - 1).getCreateTime();
            assertThat(left.isAfter(right))
                    .withFailMessage("SortType.LATEST: 최신순 정렬 실패")
                    .isTrue();
            return result;
        });
    }

    @Test
    @DisplayName("삭제")
    @Transactional
    void delete() {
        givenBoardContext();

        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);

        boardService.delete(user.getUserId(), board.getId());

        assertThatThrownBy(() -> boardService.findById(board.getId()))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("게시자가 삭제")
    @Transactional
    void deleteIsNotUser() {
        givenBoardContext();
        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
        assertThatThrownBy(() -> boardService.delete(UserId.create(), board.getId()))
                .isInstanceOf(ApiException.class)
                .hasMessage(BOARD_NOT_USER_CREATE.getMessage());
    }

    @Test
    @DisplayName("수정")
    @Transactional
    void update() {
        givenBoardContext();
        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
        String title = board.getTitle();
        String recipeIngredient = board.getContext().getBoardIngredients().get(0).getIngredient().getName();
        String description = board.getContext().getDescriptions().get(0).getDescription();

        BoardByRecipeUpdateRequest request = createDefault(board.getId(), board.getContext().getBoardIngredients(), board.getContext().getDescriptions());

        Image mainImage = imageService.uploadImageWithId(user.getUserId(), request.isMainImageChange(),
                request.getMainImageId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);
        boardRecipeService.update(user.getUserId(), request, ingredients, descriptions, mainImage);

        Board after = boardRepository.findById(board.getId()).get();

        assertAll(
                () -> assertThat(board.getId()).isEqualTo(after.getId()),
                () -> assertThat(title).isNotEqualTo(after.getTitle()),
                () -> assertThat(recipeIngredient).isNotEqualTo(after.getContext().getBoardIngredients().get(0).getIngredient().getName()),
                () -> assertThat(description).isNotEqualTo(after.getContext().getDescriptions().get(0).getDescription())
        );
    }

    @Test
    @Transactional
    void hit() {
        givenBoardContexts();
        List<User> users = new ArrayList<>();
        List<Board> board = boardRepository.findByUserId(user.getUserId()).get();
        int size = 1;

        for (int i = 0; i < 100; i++) {
            users.add(userRepository.save(UserFixture.create(i + "hitTest@gmail.com")));
            BoardUserEvent userEvent = new BoardUserEvent(board.get(size), users.get(i));
            userEvent.hitUp();
            board.get(size).addUserEvent(boardUserEventRepository.save(userEvent));
            boardRepository.save(board.get(size));
            size = random.nextInt(5);
        }

        boardService.updateUserHit(user.getUserId(), board.get(1).getId());
        Board updatedBoard = boardRepository.findById(board.get(1).getId()).get();
        assertThat(updatedBoard.getHit()).isGreaterThan(0);
    }

    @Test
    @Transactional
    void counting() {
        givenBoardContexts();

        Board before = boardRepository.findByUserId(user.getUserId()).get().get(1);
        int beforeCount = before.getCount();

        boardService.counting(boardRepository.findByUserId(user.getUserId()).get().get(1).getId());

        Board after = boardRepository.findByUserId(user.getUserId()).get().get(1);
        int afterCount = after.getCount();

        assertThat(afterCount).isEqualTo(beforeCount + 1);
    }


    private static BoardByRecipeUpdateRequest createDefault(Long boardId, List<RecipeIngredient> recipeIngredient, List<Description> Instruction) {
        Long id = boardId;
        String title = "Delicious Recipe2";
        String description = "A simple and delicious recipe.";
        List<BoardByRecipeUpdateRequest.RecipeIngredient> recipeIngredients = Arrays.asList(
                new BoardByRecipeUpdateRequest.RecipeIngredient(recipeIngredient.get(0).getId(), "update Flour", "update 200g"),
                new BoardByRecipeUpdateRequest.RecipeIngredient(recipeIngredient.get(1).getId(), "update Sugar", "update 50g")
        );

        List<BoardByRecipeUpdateRequest.Instructions> instructions = Arrays.asList(
                new BoardByRecipeUpdateRequest.Instructions(Instruction.get(0).getId(), "update Mix all ingredients", null, false),
                new BoardByRecipeUpdateRequest.Instructions(Instruction.get(1).getId(), "update Bake for 30 minutes", null, false)
        );

        return new BoardByRecipeUpdateRequest(
                id, title, description, null, false, recipeIngredients, instructions
        );
    }

    private static Stream<BoardByRecipeRequest> provideBoardCreateRequests() {
        return Stream.generate(BoardFixture::boardProvider).limit(5);
    }

    private static Stream<BoardByRecipeRequest> provideBoardFindsRequests() {
        return Stream.generate(BoardFixture::boardProvider).limit(20);
    }

    private void givenBoardContexts() {
        List<BoardByRecipeRequest> requests = provideBoardFindsRequests().toList();
        for (BoardByRecipeRequest request : requests) {

            Image mainImage = imageService.imageUpload(user.getUserId(), request.getMainImage());

            List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
            List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

            Board board = boardRecipeService.create(user.getUserId(), request, ingredients, descriptions, mainImage);
            assignRandomValues(board);
        }
    }

    private void givenBoardContext() {
        BoardByRecipeRequest request = provideBoardFindsRequests().toList().get(1);

        Image mainImage = imageService.imageUpload(user.getUserId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        Board board = boardRecipeService.create(user.getUserId(), request, ingredients, descriptions, mainImage);
        assignRandomValues(board);
//        recipeRepository.save(Recipe.ofBoard(board));
        boardRepository.save(board);
    }

    private void assignRandomValues(Board board) {
        board.updateCount(random.nextInt(1001));
        board.updateHit(random.nextInt(501));
        board.updateStar((1.0 + (random.nextInt(9) * 0.5)));
    }

}
