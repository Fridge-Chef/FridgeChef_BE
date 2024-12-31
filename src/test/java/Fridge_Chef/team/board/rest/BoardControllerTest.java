package Fridge_Chef.team.board.rest;


import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardByRecipeDeleteRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.BoardRecipeService;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.common.docs.CustomPart;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static fixture.ImageFixture.partImage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;


@DisplayName("나만의 게시판 API")
@WebMvcTest({BoardController.class, BoardsController.class})
public class BoardControllerTest extends RestDocControllerTests {
    @MockBean
    private BoardRecipeService boardRecipeService;
    @MockBean
    private BoardService boardService;
    @MockBean
    private ImageLocalService imageService;

    private User user;

    @BeforeEach
    void setup() {
        user = UserFixture.create("test@gmail.com");
    }

    @Test
    @DisplayName("추가")
    @WithMockCustomUser
    void create() throws Exception {
        when(imageService.imageUpload(any(UserId.class), any(MultipartFile.class)))
                .thenReturn(new Image("Fridge_chef.team.image-path", ImageType.ORACLE_CLOUD));

        when(boardRecipeService.uploadInstructionImages(any(UserId.class), any(), any(BoardByRecipeRequest.class)))
                .thenReturn(Collections.emptyList());

        when(boardRecipeService.findOrCreate(any(), any()))
                .thenReturn(Collections.emptyList());

        List<CustomPart> formData = List.of(
                partImage("mainImage", "메인 이미지 파일"),
                part("name", "레시피 명", "레시피 이름"),
                part("description", "레시피 설명", "레시피 설명"),
                part("dishTime", "조리 시간", "조리 시간"),
                part("dishLevel", "중", "조리 난이도"),
                part("dishCategory", "요리,카테", "조리 카테 고리(, 쉼표 구분 )"),
                part("recipeIngredients[0].name", "재료 이름", "재료 이름"),
                part("recipeIngredients[0].details", "재료 상세 정보", "상세 내용", false),
                part("recipeIngredients[1].name", "재료 이름", "재료 이름"),
                part("recipeIngredients[1].details", "재료 상세 정보", "상세 내용", false),
                part("instructions[0].content", "설명", "조리 설명"),
                partImage("instructions[0].image", "설명 이미지", false)
        );


        var post = jwtFormPostWhen("/api/board", formData);

        ResultActions actions = mockMvc.perform(post);
        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 추가",
                        jwtTokenRequest(),
                        requestPartsForm(formData)
                ));
    }

    @Test
    @DisplayName("단일 조회")
    void find() throws Exception {
        doNothing().when(boardService).counting(any(Long.class));

        when(boardService.findMyRecipeId(any(Long.class), any(Optional.class)))
                .thenReturn(createBoardMyRecipeResponse());

        ResultActions actions = jsonGetPathWhen("/api/boards/{board_id}", 1L);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 단일 조회",
                        responseFields(
                                fieldWithPath("boardId").description("레시피 ID"),
                                fieldWithPath("title").description("레시피 제목"),
                                fieldWithPath("username").description("작성자 명"),
                                fieldWithPath("myMe").description("내가 작성한 레시피 여부"),
                                fieldWithPath("description").description("레시피 소개"),
                                fieldWithPath("hitTotal").description("총 좋아요 "),
                                fieldWithPath("starTotal").description("총 별점 개수"),
                                fieldWithPath("rating").description("레시피 평점"),
                                fieldWithPath("mainImage").description("레시피 메인 이미지 URL"),
                                fieldWithPath("mainImageId").description("레시피 메인 이미지 id"),
                                fieldWithPath("dishTime").description("조리 시간"),
                                fieldWithPath("dishLevel").description("조리 난이도 "),
                                fieldWithPath("dishCategory").description("요리 카테고리"),
                                fieldWithPath("issueInfo").description("전체 : [(공백)] , 이번주 : [이번주 레시피], 이번달 :[이번달 레시피] "),
                                fieldWithPath("ownedIngredients[]").description("소유자가 소유한 재료"),
                                fieldWithPath("ownedIngredients[].id").description("재료 ID"),
                                fieldWithPath("ownedIngredients[].name").description("재료 이름"),
                                fieldWithPath("recipeIngredients[]").description("레시피에 포함된 재료"),
                                fieldWithPath("recipeIngredients[].id").description("재료 ID"),
                                fieldWithPath("recipeIngredients[].name").description("재료 이름"),
                                fieldWithPath("recipeIngredients[].details").description("재료의 세부 설명"),
                                fieldWithPath("instructions[]").description("조리 방법들"),
                                fieldWithPath("instructions[].id").description("조리 id"),
                                fieldWithPath("instructions[].content").description("설명"),
                                fieldWithPath("instructions[].imageLink").description("이미지 URL")
                        )
                ));
    }

    @Test
    @DisplayName("페이징")
    void finds() throws Exception {
        Page<BoardMyRecipePageResponse> responsePage = new PageImpl<>(
                List.of(
                        new BoardMyRecipePageResponse(SortType.RATING, 1L, "Delicious Recipe", "User1", "null", 1L, 4.5, 100, true, true, 50, LocalDateTime.now()),
                        new BoardMyRecipePageResponse(SortType.RATING, 2L, "Another Recipe", "User2", "", 22L, 4.0, 90, false, true, 50, LocalDateTime.now())
                )
        );

        when(boardService.findMyRecipes(any(), any(BoardPageRequest.class)))
                .thenReturn(responsePage);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "50");
        params.add("issue", "ALL");
        params.add("sort", "LATEST");

        ResultActions actions = jsonGetParamWhen("/api/boards", params);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 페이징 조회",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작) default: 0"),
                                parameterWithName("size").description("한 페이지에 출력할 레시피 개수 (1~50 사이즈 제한) default: 50"),
                                parameterWithName("issue").description("전체,이번주,이번달 [ ALL, THIS_WEEK , THIS_MONTH ] default : ALL"),
                                parameterWithName("sort").description("정렬 방식 " +
                                        "(예: WEEKLY_RECIPE, MONTHLY_RECIPE, LATEST ,RATING ,CLICKS ,HIT ) default: LATEST ")
                        ),
                        responseFields(
                                fieldWithPath("content[].sortType").description("정렬 방식").type(JsonFieldType.STRING),
                                fieldWithPath("content[].boardId").description("게시판 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].title").description("레시피 제목").type(JsonFieldType.STRING),
                                fieldWithPath("content[].mainImage").description("메인 이미지 링크").type(JsonFieldType.STRING),
                                fieldWithPath("content[].mainImageId").description("메인 이미지 id").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].userName").description("작성자 이름").type(JsonFieldType.STRING),
                                fieldWithPath("content[].star").description("레시피 평점").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].hit").description("조회수").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].myHit").description("내가 좋아요 클릭 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("content[].myMe").description("내가 작성한 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("content[].click").description("클릭 수").type(JsonFieldType.NUMBER),
                                fieldWithPath("content[].createTime").description("레시피 생성 시간").type(JsonFieldType.STRING),
                                fieldWithPath("page").description("페이지").type(JsonFieldType.OBJECT),
                                fieldWithPath("page.size").description("사이즈").type(JsonFieldType.NUMBER),
                                fieldWithPath("page.number").description("번호").type(JsonFieldType.NUMBER),
                                fieldWithPath("page.totalElements").description("총 요소 ").type(JsonFieldType.NUMBER),
                                fieldWithPath("page.totalPages").description("총 페이지 ").type(JsonFieldType.NUMBER)
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

        ResultActions actions = jwtJsonDeleteWhen("/api/boards/1", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 삭제",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("id").description("게시글 번호").type(JsonFieldType.NUMBER)
                        )
                ));
    }

    @Test
    @DisplayName("수정")
    @WithMockCustomUser
    void update() throws Exception {
        Image mockImage = mock(Image.class);
        List<Description> mockDescriptions = Collections.singletonList(mock(Description.class));
        List<RecipeIngredient> mockIngredients = Collections.singletonList(mock(RecipeIngredient.class));

        when(imageService.uploadImageWithId(any(UserId.class), any(Boolean.class), any(Long.class), any(MultipartFile.class)))
                .thenReturn(mockImage);

        when(boardRecipeService.uploadInstructionImages(any(UserId.class), any(BoardByRecipeUpdateRequest.class), anyList()))
                .thenReturn(mockDescriptions);

        when(boardRecipeService.findOrCreate(any(), any()))
                .thenReturn(mockIngredients);

        when(boardRecipeService.update(any(UserId.class),
                any(BoardByRecipeUpdateRequest.class)
        )).thenReturn(null);


        List<CustomPart> formData = List.of(
                part("id", "1", "레시피의 고유 ID (수정할 레시피를 식별하기 위한 필수 필드)"),
                part("title", "레시피 명", "레시피 제목", false),
                part("description", "레시피 설명", "레시피 설명", false),
                partImage("mainImage", "레시피의 메인 이미지 파일 (Optional: 이미지를 변경하려면 업로드)", false),
                part("mainImageChange", "false", "메인 이미지 변경 여부 (true: 이미지 변경, false: 유지)", false),
                part("mainImageId", "1", "기존 메인 이미지의 ID (변경할 때만 필요)", false),
                part("dishTime", "조리 시간", "조리 시간 (예: '30분')", false),
                part("dishLevel", "조리 난이도", "조리 난이도 (예: '쉬움', '보통', '어려움')", false),
                part("dishCategory", "조리 카테 고리", "조리 카테고리 ", false),
                part("recipeIngredients[0].name", "재료 이름1", "첫 번째 재료 이름 (예: '양파')"),
                part("recipeIngredients[0].details", "재료 상세 정보1", "첫 번째 재료의 상세 정보 (예: '다진 양파 100g')", false),
                part("recipeIngredients[1].name", "재료 이름 2", "두 번째 재료 이름 "),
                part("recipeIngredients[1].details", "재료 상세 정보2", "두 번째 재료의 상세 정보", false),
                part("instructions[0].id", "2", "조리 단계 이미지 변경 여부 id", false),
                part("instructions[0].content", "설명", "첫 번째 조리 단계 설명 (예: '양파를 볶는다.')", false),
                part("instructions[0].imageChange", "false", "조리 단계 이미지 변경 여부 (true: 이미지 변경, false: 유지)", false),
                partImage("instructions[0].image", "~ 번째 조리 단계 이미지 파일 (Optional: 이미지를 변경할 때만 필요)", false)
        );

        ResultActions actions = mockMvc.perform(jwtFormPutWhen("/api/board", formData));

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 수정",
                        jwtTokenRequest(),
                        requestPartsForm(formData)
                ));

    }

    public static BoardMyRecipeResponse createBoardMyRecipeResponse() {
        return new BoardMyRecipeResponse(
                "Delicious Recipe",
                false,
                "username",
                "intro",
                4.5,
                20,
                20,
                "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/notfound.png",
                1L,
                "이번주 레시피",
                "4분",
                "중",
                "한식, 빠른요리",
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
                new BoardMyRecipeResponse.StepResponse(1L, "Chop the chicken into small pieces.", "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/notfound.png"),
                new BoardMyRecipeResponse.StepResponse(2L, "Add garlic and fry until golden brown.", "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/notfound.png")
        );
    }
}
