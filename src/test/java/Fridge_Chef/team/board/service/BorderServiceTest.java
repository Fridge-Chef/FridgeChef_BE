package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import fixture.UserFixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static Fridge_Chef.team.common.UtilTest.executionTime;
import static Fridge_Chef.team.exception.ErrorCode.BOARD_NOT_USER_CREATE;
import static org.assertj.core.api.Assertions.*;
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
    void create(BoardByRecipeRequest request) {
        Image mainImage = imageService.imageUpload(user.getUserId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        boardRecipeService.create(user.getUserId(), request, ingredients, descriptions, mainImage);
    }


    @Test
    @DisplayName("단일 검색")
    void find() {
        givenBoardContext();
        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
        BoardMyRecipeResponse response = boardService.findMyRecipeId(board.getId());
        assertAll(() -> board.getId().equals(response.getBoardId()),
                () -> board.getTitle().equals(response.getTitle()));
    }

    @Test
    @DisplayName("페이징")
    void finds() {
        givenBoardContexts();
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(1, 50, SortType.HIT);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(request);
            int left = result.getContent().get(0).getHit();
            int right = result.getContent().get(result.getSize() - 1).getHit();
            assertThat(left > right)
                    .withFailMessage("SortType.HIT: 좋아요순 정렬 실패")
                    .isTrue();
            return result;
        });
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(1, 50, SortType.CLICKS);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(request);
            int left = result.getContent().get(0).getClick();
            int right = result.getContent().get(result.getSize() - 1).getClick();
            assertThat(left > right)
                    .withFailMessage("SortType.CLICKS: 클릭순 정렬 실패")
                    .isTrue();
            return result;
        });
        executionTime(() -> {
            BoardPageRequest request = new BoardPageRequest(1, 50, SortType.LATEST);
            Page<BoardMyRecipePageResponse> result = boardService.findMyRecipes(request);
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
    void delete() {
        givenBoardContext();

        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);

        boardService.delete(user.getUserId(), board.getId());

        assertThatThrownBy(() -> boardService.findById(board.getId()))
                .isInstanceOf(ApiException.class);
    }

    @Test
    @DisplayName("게시자가 삭제")
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
        boardRecipeService.update(user.getUserId(),request, ingredients, descriptions, mainImage);

        Board after = boardRepository.findById(board.getId()).get();

        assertAll(
                () -> assertThat(board.getId()).isEqualTo(after.getId()),
                () -> assertThat(title).isNotEqualTo(after.getTitle()),
                () -> assertThat(recipeIngredient).isNotEqualTo(after.getContext().getBoardIngredients().get(0).getIngredient().getName()),
                () -> assertThat(description).isNotEqualTo(after.getContext().getDescriptions().get(0).getDescription())
        );
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
                id, title, description, null,false, recipeIngredients, instructions
        );
    }

    private static Stream<BoardByRecipeRequest> provideBoardCreateRequests() {
        return Stream.generate(BorderServiceTest::boardProvider).limit(5);
    }

    private static Stream<BoardByRecipeRequest> provideBoardFindsRequests() {
        return Stream.generate(BorderServiceTest::boardProvider).limit(300);
    }

    private void givenBoardContexts() {
        List<BoardByRecipeRequest> requests = provideBoardFindsRequests().toList();
        for (BoardByRecipeRequest request : requests) {

            Image mainImage = imageService.imageUpload(user.getUserId(), request.getMainImage());

            List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
            List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

            Board board = boardRecipeService.create(user.getUserId(), request, ingredients, descriptions, mainImage);
            assignRandomValues(board);
            boardRepository.save(board);
        }
    }

    private void givenBoardContext() {
        BoardByRecipeRequest request = provideBoardFindsRequests().toList().get(1);

        Image mainImage = imageService.imageUpload(user.getUserId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        Board board = boardRecipeService.create(user.getUserId(), request, ingredients, descriptions, mainImage);
        assignRandomValues(board);
        boardRepository.save(board);
    }

    private void assignRandomValues(Board board) {
        board.updateCount(random.nextInt(1001));
        board.updateHit(random.nextInt(501));
        board.updateStar((1.0 + (random.nextInt(9) * 0.5)));
    }

    public static BoardByRecipeRequest boardProvider() {
        MockMultipartFile mainImage = new MockMultipartFile("mainImage", "Fridge_chef.team.image" + random.nextInt(1000) + ".jpg", "image/jpeg", "dummy Fridge_chef.team.image content".getBytes());
        MockMultipartFile instructionImage = new MockMultipartFile("instructionImage", "step" + random.nextInt(1000) + ".jpg", "image/jpeg", "dummy Fridge_chef.team.image content".getBytes());

        String recipeTitle = RECIPE_TITLES.get(random.nextInt(RECIPE_TITLES.size())) + " " + CORE_RECIPE_NAMES.get(random.nextInt(CORE_RECIPE_NAMES.size())) + " " + ADDITIONAL_RECIPE_NAMES.get(random.nextInt(ADDITIONAL_RECIPE_NAMES.size()));
        String recipeDescription = COOKING_STEPS.get(random.nextInt(COOKING_STEPS.size()));

        List<BoardByRecipeRequest.RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String ingredientName = INGREDIENTS.get(random.nextInt(INGREDIENTS.size()));
            String quantity = (random.nextInt(500) + 50) + "g";
            recipeIngredients.add(new BoardByRecipeRequest.RecipeIngredient(ingredientName, quantity));
        }

        List<BoardByRecipeRequest.Instructions> instructions = new ArrayList<>();
        for (BoardByRecipeRequest.RecipeIngredient recipeIngredient : recipeIngredients) {
            String stepDescription = recipeIngredient.getName() + "을(를) 사용해 " + recipeDescription;
            instructions.add(new BoardByRecipeRequest.Instructions(stepDescription, instructionImage));
        }

        return new BoardByRecipeRequest(
                recipeTitle,
                recipeDescription,
                mainImage,
                recipeIngredients,
                instructions
        );
    }

    private static final List<String> INGREDIENTS = Arrays.asList(
            "라면", "김치", "참치", "당근", "양파", "감자", "계란", "우유", "소금", "고추장",
            "대파", "고구마", "소고기", "닭고기", "돼지고기", "마늘", "고춧가루", "참기름", "간장", "설탕",
            "버섯", "미역", "치즈", "스파게티", "햄", "깻잎", "연두부", "콩나물", "멸치", "된장",
            "쌀", "참깨", "새우", "오징어", "조개", "해파리", "토마토", "양상추", "버터", "브로콜리",
            "베이컨", "연어", "떡", "쌀국수", "굴소스", "두부", "감자전분", "초콜릿", "아몬드", "피망"
    );

    private static final List<String> COOKING_STEPS = Arrays.asList(
            "맛있게 조리한다.", "약불에서 천천히 익힌다.", "고소하게 볶는다.", "재료를 잘 섞는다.",
            "끓는 물에 넣고 푹 끓인다.", "바삭하게 튀긴다.", "구수한 맛이 나도록 한다.", "재료를 골고루 버무린다.",
            "풍미가 가득해질 때까지 기다린다.", "재료가 잘 익을 때까지 구워준다.", "재료를 부드럽게 다진다.",
            "새콤달콤하게 양념한다.", "기름에 잘 구워준다.", "냄비에 넣고 졸여준다.", "따뜻한 불에서 구워낸다.",
            "촉촉하게 익힌다.", "감칠맛이 나도록 소스를 더한다.", "쫄깃쫄깃한 식감을 살린다.", "향이 풍부하게 피어오를 때까지 끓인다.",
            "재료의 신선함을 유지한다."
    );

    private static final List<String> RECIPE_TITLES = Arrays.asList(
            "맛있는", "간단한", "빠르게 만드는", "특별한", "정성이 가득한", "집에서 간편하게", "풍부한 맛의", "촉촉한",
            "고소한", "영양이 가득한", "매콤한", "담백한", "기름기 없는", "풍미 가득한", "건강한", "고급스러운",
            "신선한 재료로 만든", "상큼한", "쫄깃한", "따뜻한"
    );

    private static final List<String> CORE_RECIPE_NAMES = Arrays.asList(
            "라면", "김치찌개", "참치 마요 덮밥", "된장국", "비빔밥", "볶음밥", "스파게티", "샐러드", "카레", "탕수육",
            "김밥", "떡볶이", "잡채", "삼겹살 구이", "닭갈비", "계란말이", "파스타", "찜닭", "갈비찜", "초밥",
            "불고기", "순두부찌개", "떡국", "콩나물국", "갈비탕", "육개장", "오므라이스", "부대찌개", "감자탕", "수제비",
            "라자냐", "찹쌀떡", "쌀국수", "닭볶음탕", "전복죽", "오리백숙", "도토리묵", "북엇국", "나물비빔밥", "코다리찜",
            "된장찌개", "홍합탕", "우동", "해물파전", "청국장", "고등어구이", "닭강정", "애호박전", "두부조림", "낙지볶음",
            "조기찜", "돼지불백", "갈치조림", "오징어덮밥", "생선가스", "닭가슴살 샐러드", "장어덮밥", "카프레제", "치킨커리", "훈제연어",
            "토마토 스파게티", "봉골레 파스타", "차돌박이 샤브샤브", "새우튀김", "돈가스", "유부초밥", "매운갈비찜", "짜장면", "짬뽕", "양념치킨",
            "탕탕이", "굴국밥", "매생이국", "김치볶음밥", "소불고기", "문어숙회", "차돌된장찌개", "갈릭버터 새우", "불닭볶음면", "장조림",
            "낙지소면", "어묵탕", "브리또", "찜갈비", "바비큐폭립", "돼지갈비", "게살스프", "시금치프리타타", "고추장불고기", "샤브샤브",
            "감바스", "수제비", "닭가슴살 구이", "양갈비 스테이크", "바지락칼국수", "칠리새우", "우럭찜", "미소된장국", "비프스튜", "해물찜"
    );

    private static final List<String> ADDITIONAL_RECIPE_NAMES = Arrays.asList(
            "특제 소스", "달콤한 양념", "고소한 참기름", "매콤한 맛", "정통 스타일", "집에서", "풍성한 재료", "엄마의 손맛", "전통 방식",
            "간편 레시피", "신선한 재료", "초보 요리사용", "프로 요리사용", "아이들이 좋아하는", "어른들도 좋아하는", "혼자 먹기 좋은"
    );
}
