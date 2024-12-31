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
import Fridge_Chef.team.board.service.request.RecipeIngredientDto;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.common.BootTest;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import fixture.BoardFixture;
import fixture.CommentFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static Fridge_Chef.team.common.UtilTest.executionTime;
import static Fridge_Chef.team.exception.ErrorCode.BOARD_NOT_USER_CREATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("레시피 서비스")
public class BorderServiceTest extends BootTest {
    private static final Random random = new Random();
    @Autowired
    private ImageLocalService imageService;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardUserEventRepository boardUserEventRepository;
    @Autowired
    private BoardRecipeService boardRecipeService;
    @Autowired
    private BoardService boardService;
    private User user;
    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserFixture.create("test@gmail.com"));
    }

    @ParameterizedTest
    @MethodSource("provideBoardCreateRequests")
    @DisplayName("추가")
    @Transactional
    void create(BoardByRecipeRequest request) {
        imageService.imageUpload(user.getUserId(), request.getMainImage());
        boardRecipeService.uploadInstructionImages(user.getUserId(), null,request);
        boardRecipeService.create(user.getUserId(), request);
    }


    @Test
    @DisplayName("단일 검색")
    @Transactional
    void find() {
        givenBoardContext();
        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
        BoardMyRecipeResponse response = boardService.findMyRecipeId(board.getId(), Optional.of(UserId.create()));
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
            int left = result.getContent().get(0).hit();
            int right = result.getContent().get(result.getSize() - 1).hit();
            assertThat(left > right)
                    .withFailMessage("SortType.HIT: 좋아요순 정렬 실패")
                    .isTrue();
            return result;
        });
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(0, 20, IssueType.ALL, SortType.CLICKS);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(user.getUserId(), request);
            int left = result.getContent().get(0).click();
            int right = result.getContent().get(result.getSize() - 1).click();
            assertThat(left > right)
                    .withFailMessage("SortType.CLICKS: 클릭순 정렬 실패")
                    .isTrue();
            return result;
        });
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(0, 20, IssueType.ALL, SortType.LATEST);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(user.getUserId(), request);
            LocalDateTime left = result.getContent().get(0).createTime();
            LocalDateTime right = result.getContent().get(result.getSize() - 1).createTime();
            assertThat(left.isAfter(right))
                    .withFailMessage("SortType.LATEST: 최신순 정렬 실패")
                    .isTrue();
            return result;
        });

        BoardPageRequest request = new BoardPageRequest(0, 51, IssueType.ALL, SortType.LATEST);
        assertThatThrownBy(() -> boardService.findMyRecipes(user.getUserId(), request))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("Oracle image 삭제")
    @Transactional
    void deleteOracle() {
        givenBoardContext();

        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
        board.updateMainImage(imageRepository.save(new Image("", ImageType.ORACLE_CLOUD)));
        board.updateContext(
                List.of(
                        new Description("", new Image("", ImageType.ORACLE_CLOUD)),
                        new Description("", new Image("", ImageType.ORACLE_CLOUD))
                )
                , "", "", "", "", ""
        );
        Comment comment1 = CommentFixture.create(board, user);
        Comment comment2 = CommentFixture.create(board, user);
        comment1.updateImage(List.of(new Image("", ImageType.ORACLE_CLOUD)));
        comment2.updateImage(List.of(new Image("", ImageType.ORACLE_CLOUD)));
        board.updateComment(List.of(comment2, comment1));
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
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        recipeIngredients.add(addRecipeIngredient(new RecipeIngredientDto(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())) + "2", "디테일스2"),board));
        recipeIngredients.add(addRecipeIngredient(new RecipeIngredientDto(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())) + "2", "디테일스2"),board));
        recipeIngredients.add(addRecipeIngredient(new RecipeIngredientDto(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())) + "2", "디테일스2"),board));
        recipeIngredients.add(addRecipeIngredient(new RecipeIngredientDto(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())) + "2", "디테일스2"),board));

        BoardByRecipeUpdateRequest request = createDefault(board.getId(),
                recipeIngredients,
                board.getContext().getDescriptions());

        boardRecipeService.update(board.getUser().getUserId(), request);
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
            Board board = boardRecipeService.create(user.getUserId(), request);
            List<Description> descriptions = boardRecipeService.uploadInstructionImages(user.getUserId(),board, request);
            List<RecipeIngredient> ingredients = boardRecipeService.findOrCreate(List.of(
                            new BoardByRecipeRequest.RecipeIngredient(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())), "재료1"),
                            new BoardByRecipeRequest.RecipeIngredient(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())), "재료2"),
                            new BoardByRecipeRequest.RecipeIngredient(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())), "재료3")
                    ),board
            );
            board.updateContext(ingredients, descriptions, "", "", "");
            assignRandomValues(board);
        }
    }

    private void givenBoardContext() {
        BoardByRecipeRequest request = provideBoardFindsRequests().toList().get(1);

        Image mainImage = imageRepository.save(Image.none());
        Board board = boardRecipeService.create(user.getUserId(), request);
        List<Description> descriptions = boardRecipeService.uploadInstructionImages(user.getUserId(),board, request);
        List<RecipeIngredient> ingredients = boardRecipeService.findOrCreate(List.of(
                        new BoardByRecipeRequest.RecipeIngredient(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())), "재료1"),
                        new BoardByRecipeRequest.RecipeIngredient(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())), "재료2"),
                        new BoardByRecipeRequest.RecipeIngredient(INGREDIENTS.get(random.nextInt(INGREDIENTS.size())), "재료3")
                ),board
        );
        board.updateContext(ingredients, descriptions, "1분", "보통", "간식,강아지");

        assignRandomValues(board);
        boardRepository.save(board);
    }

    private static final List<String> INGREDIENTS = Arrays.asList(
            "라면", "김치", "참치", "당근", "양파", "감자", "계란", "우유", "소금", "고추장",
            "대파", "고구마", "소고기", "닭고기", "돼지고기", "마늘", "고춧가루", "참기름", "간장", "설탕",
            "버섯", "미역", "치즈", "스파게티", "햄", "깻잎", "연두부", "콩나물", "멸치", "된장",
            "쌀", "참깨", "새우", "오징어", "조개", "해파리", "토마토", "양상추", "버터", "브로콜리",
            "베이컨", "연어", "떡", "쌀국수", "굴소스", "두부", "감자전분", "초콜릿", "아몬드", "피망"
    );

    private void assignRandomValues(Board board) {
        board.updateCount(random.nextInt(1001));
        board.updateHit(random.nextInt(501));
        board.updateStar((1.0 + (random.nextInt(9) * 0.5)));
    }

    @Transactional
    public RecipeIngredient addRecipeIngredient(RecipeIngredientDto dto,Board board) {
        return boardRecipeService.findOrSaveIngredient(dto,board);
    }
}
