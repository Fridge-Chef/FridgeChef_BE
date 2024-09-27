package Fridge_Chef.team.board.rest;


import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardType;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.service.BoardIngredientService;
import Fridge_Chef.team.board.service.BoardRecipeService;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.board.service.BorderServiceTest;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.service.ImageLocalService;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.UserController;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import static Fridge_Chef.team.exception.ErrorCode.BOARD_NOT_USER_CREATE;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@DisplayName("나만의 게시판")
@WebMvcTest(BoardController.class)
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
                .thenReturn(Collections.emptyList()); // 실제 리스트 반환

        when(boardIngredientService.findOrCreate(any(BoardByRecipeRequest.class)))
                .thenReturn(Collections.emptyList()); // 실제 리스트 반환

        ResultActions actions = jwtJsonPostWhen("/api/board", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피 추가",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("name").description("게시판 이름").optional(),
                                fieldWithPath("description").description("게시판 설명"),
                                fieldWithPath("mainImage").description("게시판 메인 이미지"),
                                fieldWithPath("recipeIngredients[].name").description("레시피 재료 이름"),
                                fieldWithPath("recipeIngredients[].details").description("레시피 재료 설명"),
                                fieldWithPath("instructions[].content").description("조리법 설명"),
                                fieldWithPath("instructions[].image").description("조리법 이미지 (파일)")
                        )
                ));
    }


//    @Test
//    @DisplayName("단일 검색")
//    void find() {
//    }
//
//    @Test
//    @DisplayName("페이징")
//    void finds() {
//    }
//
//    @Test
//    @DisplayName("삭제")
//    void delete() {
////        Board board = boardRepository.findByUserId(user.getUserId()).get().get(0);
////        boardService.delete(user.getUserId(), board.getId());
//    }
//
//    @Test
//    @DisplayName("수정")
//    void update() {
////        updateRecipe(boardRepository.findByUserId(user.getUserId()).get().get(0));
//    }


    public static BoardByRecipeRequest createBoardByRecipeRequest() {

        // MultipartFile을 위한 MockMultipartFile 생성
        MockMultipartFile mainImage = null;
        // 샘플 레시피 재료 목록 생성
        List<BoardByRecipeRequest.RecipeIngredient> recipeIngredients = List.of(
                new BoardByRecipeRequest.RecipeIngredient("Ingredient 1", "Detail 1"),
                new BoardByRecipeRequest.RecipeIngredient("Ingredient 2", "Detail 2")
        );

        // 샘플 레시피 설명 목록 생성
        List<BoardByRecipeRequest.Instructions> instructions = List.of(
                new BoardByRecipeRequest.Instructions("Step 1", null),
                new BoardByRecipeRequest.Instructions("Step 2",null)
        );

        return new BoardByRecipeRequest(
                "Test Recipe",
                "This is a test recipe",
                null,
                recipeIngredients,
                instructions
        );
    }

    private void updateRecipe(Board board) {
        BoardByRecipeUpdateRequest request = createDefault(board.getId(), board.getContext().getBoardIngredients(), board.getContext().getDescriptions());
        Image mainImage = imageService.uploadImageWithId(user.getUserId(), request.isMainImageChange(),
                request.getMainImageId(), request.getMainImage());
        boardRecipeService.update(user.getUserId(), request,
                boardIngredientService.findOrCreate(request),
                boardIngredientService.uploadInstructionImages(user.getUserId(), request),
                mainImage);
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
        return Stream.generate(BorderServiceTest::boardProvider).limit(5);
    }

    private static Stream<BoardByRecipeRequest> provideBoardFindsRequests() {
        return Stream.generate(BorderServiceTest::boardProvider).limit(100);
    }

    private void givenBoardContexts() {
        List<BoardByRecipeRequest> requests = provideBoardFindsRequests().toList();
        for (BoardByRecipeRequest request : requests) {

            System.out.println(user.getUserId().getValue());
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

    public static String encodeFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

}
