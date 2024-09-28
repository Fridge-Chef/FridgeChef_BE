package Fridge_Chef.team.board.rest;


import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardByRecipeDeleteRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.BoardIngredientService;
import Fridge_Chef.team.board.service.BoardRecipeService;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@DisplayName("나만의 게시판")
@WebMvcTest({BoardController.class, BoardsController.class})
public class BoardControllerTest extends RestDocControllerTests {
    private static final Random random = new Random();
    @MockBean
    private BoardRecipeService boardRecipeService;
    @MockBean
    private BoardIngredientService boardIngredientService;
    @MockBean
    private BoardService boardService;
    @MockBean
    private ImageLocalService imageService;
    @MockBean
    private BoardRepository boardRepository;
    @MockBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        user = UserFixture.createId("test@gmail.com");
    }

    @Test
    @DisplayName("추가")
    @WithMockCustomUser
    void create() throws Exception {
        BoardByRecipeRequest boardByRecipeRequest = createBoardByRecipeRequest();

        String request = objectMapper.writeValueAsString(boardByRecipeRequest);

        when(imageService.imageUpload(any(UserId.class), any(MultipartFile.class)))
                .thenReturn(new Image("Fridge_chef.team.image-path", ImageType.ORACLE_CLOUD));

        when(boardIngredientService.uploadInstructionImages(any(UserId.class), any(BoardByRecipeRequest.class)))
                .thenReturn(Collections.emptyList());

        when(boardIngredientService.findOrCreate(any(BoardByRecipeRequest.class)))
                .thenReturn(Collections.emptyList());

        ResultActions actions = jwtJsonPostWhen("/api/board", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 추가",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("name").description("레시피 이름").optional(),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("mainImage").description("레시피 메인 이미지"),
                                fieldWithPath("recipeIngredients[].name").description("레시피 재료 이름"),
                                fieldWithPath("recipeIngredients[].details").description("레시피 재료 설명"),
                                fieldWithPath("instructions[].content").description("조리법 설명"),
                                fieldWithPath("instructions[].image").description("조리법 이미지 (파일)")
                        )
                ));
    }

    @Test
    @DisplayName("단일 조회")
    void find() throws Exception {
        doNothing().when(boardService).counting(any(Long.class));

        when(boardService.findMyRecipeId(any(Long.class)))
                .thenReturn(createBoardMyRecipeResponse());

        ResultActions actions = jsonGetPathWhen("/api/boards/{board_id}", 1L);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 단일 조회",
                        responseFields(
                                fieldWithPath("title").description("레시피 제목"),
                                fieldWithPath("rating").description("레시피 평점"),
                                fieldWithPath("mainImage").description("레시피 메인 이미지 URL"),
                                fieldWithPath("ownedIngredients[].id").description("사용자가 소유한 재료 ID"),
                                fieldWithPath("ownedIngredients[].name").description("사용자가 소유한 재료 이름"),
                                fieldWithPath("recipeIngredients[].id").description("레시피에 포함된 재료 ID"),
                                fieldWithPath("recipeIngredients[].name").description("레시피에 포함된 재료 이름"),
                                fieldWithPath("recipeIngredients[].details").description("레시피 재료의 세부 설명"),
                                fieldWithPath("instructions[].content").description("레시피 조리법 설명"),
                                fieldWithPath("instructions[].imageLink").description("조리법 이미지 URL"),
                                fieldWithPath("boardId").description("레시피가 속한 게시판 ID")
                        )
                ));
    }

    @Test
    @DisplayName("페이징")
    void finds() throws Exception {
        BoardPageRequest boardByRecipeRequest = new BoardPageRequest(1, 10, SortType.RATING);
        Page<BoardMyRecipePageResponse> responsePage = new PageImpl<>(
                List.of(
                        new BoardMyRecipePageResponse(SortType.RATING, 1L, "Delicious Recipe", "User1", 4.5, 100, 50, LocalDateTime.now()),
                        new BoardMyRecipePageResponse(SortType.RATING, 2L, "Another Recipe", "User2", 4.0, 90, 45, LocalDateTime.now())
                )
        );

        when(boardService.findMyRecipes(any(BoardPageRequest.class)))
                .thenReturn(responsePage);
        String request = objectMapper.writeValueAsString(boardByRecipeRequest);

        ResultActions actions = jsonGetWhen("/api/boards", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 페이징 조회",
                        requestFields(
                                fieldWithPath("page").description("페이지 번호 (0부터 시작)"),
                                fieldWithPath("size").description("한 페이지에 출력할 레시피 개수 (1~50 사이즈 제한)"),
                                fieldWithPath("sortType").description("정렬 방식 " +
                                        "(예: WEEKLY_RECIPE, MONTHLY_RECIPE, LATEST ,RATING ,CLICKS ,HIT )").optional()
                        ),
                        responseFields(
                                fieldWithPath("content[].sortType").description("정렬 방식"),
                                fieldWithPath("content[].boardId").description("게시판 ID"),
                                fieldWithPath("content[].title").description("레시피 제목"),
                                fieldWithPath("content[].userName").description("작성자 이름"),
                                fieldWithPath("content[].star").description("레시피 평점"),
                                fieldWithPath("content[].hit").description("조회수"),
                                fieldWithPath("content[].click").description("클릭 수"),
                                fieldWithPath("content[].createTime").description("레시피 생성 시간"),
                                fieldWithPath("pageable").description("페이지 관련 정보"),
                                fieldWithPath("totalElements").description("총 레시피 수"),
                                fieldWithPath("totalPages").description("총 페이지 수"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("size").description("현재 페이지의 사이즈"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort.sorted").description("정렬 여부"),
                                fieldWithPath("sort.unsorted").description("비정렬 여부"),
                                fieldWithPath("sort.empty").description("-"),
                                fieldWithPath("first").description("첫 페이지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지에 포함된 요소 수"),
                                fieldWithPath("empty").description("비어있는 페이지 여부")
                        )
                ));
    }

    @Test
    @DisplayName("삭제")
    @WithMockCustomUser
    void delete() throws Exception {
        BoardByRecipeDeleteRequest boardByRecipeRequest = new BoardByRecipeDeleteRequest(1L);
        String request = objectMapper.writeValueAsString(boardByRecipeRequest);

        doNothing().when(boardService).delete(any(UserId.class), any(Long.class));

        ResultActions actions = jwtJsonDeleteWhen("/api/board", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 삭제",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("id").description("게시글 번호")
                        )
                ));
    }

    @Test
    @DisplayName("수정")
    @WithMockCustomUser
    void update() throws Exception {
        BoardByRecipeUpdateRequest boardByRecipeRequest = createUpdateRequest(1L, true);

        String request = objectMapper.writeValueAsString(boardByRecipeRequest);

        Image mockImage = mock(Image.class);
        List<Description> mockDescriptions = Collections.singletonList(mock(Description.class));
        List<RecipeIngredient> mockIngredients = Collections.singletonList(mock(RecipeIngredient.class));

        when(imageService.uploadImageWithId(any(UserId.class), any(Boolean.class), any(Long.class), any(MultipartFile.class)))
                .thenReturn(mockImage);

        when(boardIngredientService.uploadInstructionImages(any(UserId.class), any(BoardByRecipeUpdateRequest.class)))
                .thenReturn(mockDescriptions);

        when(boardIngredientService.findOrCreate(any(BoardByRecipeUpdateRequest.class)))
                .thenReturn(mockIngredients);

        when(boardRecipeService.update(any(UserId.class),
                any(BoardByRecipeUpdateRequest.class),
                anyList(),
                anyList(),
                any(Image.class)
        )).thenReturn(null);


        ResultActions actions = jwtJsonPatchWhen("/api/board", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 수정",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("id").description("레시피 ID").optional(),
                                fieldWithPath("title").description("레시피 제목"),
                                fieldWithPath("description").description("레시피 설명"),
                                fieldWithPath("mainImage").description("업로드할 메인 이미지"),
                                fieldWithPath("mainImageId").description("기존 메인 이미지 ID"),
                                fieldWithPath("mainImageChange").type(JsonFieldType.BOOLEAN).description("메인 이미지 변경 여부"),
                                fieldWithPath("recipeIngredients[].id").description("재료 ID"),
                                fieldWithPath("recipeIngredients[].name").description("재료 이름"),
                                fieldWithPath("recipeIngredients[].details").description("재료 상세 설명"),
                                fieldWithPath("instructions[].id").description("조리법 ID"),
                                fieldWithPath("instructions[].content").description("조리법 내용"),
                                fieldWithPath("instructions[].image").description("조리법 이미지"),
                                fieldWithPath("instructions[].imageChange").description("조리법 이미지 변경 여부")
                        )
                ));

    }

    public static BoardByRecipeUpdateRequest createUpdateRequest(Long recipeId, boolean isMainImageChange) {
        MultipartFile mockMainImage = null;
        List<BoardByRecipeUpdateRequest.RecipeIngredient> recipeIngredients = createUpdateRecipeIngredients();
        List<BoardByRecipeUpdateRequest.Instructions> instructions = createUpdateInstructions();

        return new BoardByRecipeUpdateRequest(
                recipeId,
                "Updated Recipe Title",
                "Updated Recipe Description",
                mockMainImage,
                2L,
                isMainImageChange,
                recipeIngredients,
                instructions
        );
    }

    private static List<BoardByRecipeUpdateRequest.RecipeIngredient> createUpdateRecipeIngredients() {
        return Arrays.asList(
                new BoardByRecipeUpdateRequest.RecipeIngredient(1L, "Chicken", "500g"),
                new BoardByRecipeUpdateRequest.RecipeIngredient(2L, "Salt", "1 tsp")
        );
    }

    private static List<BoardByRecipeUpdateRequest.Instructions> createUpdateInstructions() {
        return Arrays.asList(
                new BoardByRecipeUpdateRequest.Instructions(1L, "Chop the ingredients.", null, false),
                new BoardByRecipeUpdateRequest.Instructions(2L, "Cook over medium heat.", null, false)
        );
    }


    public static BoardByRecipeRequest createBoardByRecipeRequest() {

        MockMultipartFile mainImage = null;
        List<BoardByRecipeRequest.RecipeIngredient> recipeIngredients = List.of(
                new BoardByRecipeRequest.RecipeIngredient("Ingredient 1", "Detail 1"),
                new BoardByRecipeRequest.RecipeIngredient("Ingredient 2", "Detail 2")
        );

        List<BoardByRecipeRequest.Instructions> instructions = List.of(
                new BoardByRecipeRequest.Instructions("Step 1", null),
                new BoardByRecipeRequest.Instructions("Step 2", null)
        );

        return new BoardByRecipeRequest(
                "Test Recipe",
                "This is a test recipe",
                null,
                recipeIngredients,
                instructions
        );
    }

    public static BoardMyRecipeResponse createBoardMyRecipeResponse() {
        return new BoardMyRecipeResponse(
                "Delicious Recipe",
                4.5,
                "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/notfound.png",
                createOwnedIngredients(),
                createRecipeIngredients(),
                createInstructions(),
                1L
        );
    }

    private static List<BoardMyRecipeResponse.OwnedIngredientResponse> createOwnedIngredients() {
        return List.of(
                new BoardMyRecipeResponse.OwnedIngredientResponse(1L, "Salt"),
                new BoardMyRecipeResponse.OwnedIngredientResponse(2L, "Pepper")
        );
    }

    private static List<BoardMyRecipeResponse.RecipeIngredientResponse> createRecipeIngredients() {
        return List.of(
                new BoardMyRecipeResponse.RecipeIngredientResponse(1L, "Chicken", "500g"),
                new BoardMyRecipeResponse.RecipeIngredientResponse(2L, "Garlic", "3 cloves")
        );
    }

    private static List<BoardMyRecipeResponse.StepResponse> createInstructions() {
        return List.of(
                new BoardMyRecipeResponse.StepResponse("Chop the chicken into small pieces.", "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/notfound.png"),
                new BoardMyRecipeResponse.StepResponse("Add garlic and fry until golden brown.", "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/notfound.png")
        );
    }
}
