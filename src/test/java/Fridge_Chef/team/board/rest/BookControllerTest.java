package Fridge_Chef.team.board.rest;

import Fridge_Chef.team.board.repository.BookDslRepository;
import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BookCommentRequest;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.board.rest.response.BookCommentResponse;
import Fridge_Chef.team.board.service.BookService;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import fixture.BoardFixture;
import fixture.CommentFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

@DisplayName("레시피 북 API")
@WebMvcTest(BookController.class)
public class BookControllerTest extends RestDocControllerTests {
    @MockBean
    private BookService bookService;
    @MockBean
    private BookDslRepository bookDslRepository;
    @MockBean
    private UserRepository userRepository;
    private User user;


    @BeforeEach
    void setup() {
        user = UserFixture.createId("test@gmail.com");
    }


    @Test
    @WithMockCustomUser
    @DisplayName("찜하기 페이징 조회")
    void find() throws Exception {
        when(bookService.selectBoards(any(UserId.class), any(BookRecipeRequest.class)))
                .thenReturn(likePagesProvider());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "50");
        params.add("book", "MYRECIPE");
        params.add("sort", "LATEST");

        ResultActions actions = jwtJsonGetParamWhen("/api/books/recipe",params);

        actions.andExpect(status().isOk())
                .andDo(document("좋아요",
                        jwtTokenRequest(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("한 페이지에 출력할 레시피 개수 (1~50 사이즈 제한)"),
                                parameterWithName("book").description("북 타입 : (좋아요,나만의레시피,후기) [LIKE,MYRECIPE,LIKE]"),
                                parameterWithName("sort").description("정렬 방식 (예: WEEKLY_RECIPE, MONTHLY_RECIPE, LATEST ,RATING ,CLICKS ,HIT )").optional()
                        ),
                        responseFields(
                                fieldWithPath("content[]").description("책 목록"),
                                fieldWithPath("content[].id").description("ID 게시글"),
                                fieldWithPath("content[].mainImageLink").description("이미지 주소"),
                                fieldWithPath("content[].title").description("레시피 이름"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].hit").description("하트"),
                                fieldWithPath("page").description("페이지"),
                                fieldWithPath("page.size").description("사이즈"),
                                fieldWithPath("page.number").description("번호"),
                                fieldWithPath("page.totalElements").description("총 요소 "),
                                fieldWithPath("page.totalPages").description("총 페이지 ")
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("나의 레시피 페이징 조회")
    void findMyRecipe() throws Exception {
        when(bookService.selectBoards(any(UserId.class), any(BookRecipeRequest.class)))
                .thenReturn(likePagesProvider());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "50");
        params.add("book", "MYRECIPE");
        params.add("sort", "LATEST");

        ResultActions actions = jwtJsonGetParamWhen("/api/books/recipe", params);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피,",
                        jwtTokenRequest(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 "),
                                parameterWithName("size").description("사이즈 크기 "),
                                parameterWithName("book").description("북 타입 : (좋아요,나만의레시피,후기) [LIKE,MYRECIPE,LIKE]"),
                                parameterWithName("sort").description("정렬 타입 :(최신순,별점순,클릭순,좋아요순) [ LATEST, RATING , CLICKS, HIT ] ")
                        ),
                        responseFields(
                                fieldWithPath("content[]").description("책 목록"),
                                fieldWithPath("content[].id").description("ID 게시글"),
                                fieldWithPath("content[].mainImageLink").description("이미지 주소"),
                                fieldWithPath("content[].title").description("레시피 이름"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].hit").description("하트"), fieldWithPath("page").description("페이지"),
                                fieldWithPath("page").description("페이지"),
                                fieldWithPath("page.size").description("사이즈"),
                                fieldWithPath("page.number").description("번호"),
                                fieldWithPath("page.totalElements").description("총 요소 "),
                                fieldWithPath("page.totalPages").description("총 페이지 ")
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("레시피 후기 페이징 조회")
    void findComments() throws Exception {
        when(bookService.selectComment(any(UserId.class), any(BookCommentRequest.class)))
                .thenReturn(commentPagesProvider());

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", "0");
        params.add("size", "50");
        params.add("sort", "LATEST");

        ResultActions actions = jwtJsonGetParamWhen("/api/books/comment", params);

        actions.andExpect(status().isOk())
                .andDo(document("레시피 후기",
                        jwtTokenRequest(),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 "),
                                parameterWithName("size").description("사이즈 크기 "),
                                parameterWithName("sort").description("정렬 타입 :(최신순,별점순,클릭순,좋아요순) [ LATEST, RATING , CLICKS, HIT ] ")
                        ),
                        responseFields(
                                fieldWithPath("content[]").description("코멘트 목록"),
                                fieldWithPath("content[].boardId").description("게시글 ID"),
                                fieldWithPath("content[].commentId").description("코멘트 ID"),
                                fieldWithPath("content[].name").description("작성자 이름"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].context").description("코멘트 내용"),
                                fieldWithPath("page").description("페이지"),
                                fieldWithPath("page.size").description("사이즈"),
                                fieldWithPath("page.number").description("번호"),
                                fieldWithPath("page.totalElements").description("총 요소 "),
                                fieldWithPath("page.totalPages").description("총 페이지 ")
                        )
                ));
    }

    private Page<BookCommentResponse> commentPagesProvider() {
        User user1 = UserFixture.create("test1@gmail.com");
        User user2 = UserFixture.create("test2@gmail.com");
        List<BookCommentResponse> responses = List.of(
                new BookCommentResponse(CommentFixture.create(BoardFixture.create(user1), user1)),
                new BookCommentResponse(CommentFixture.create(BoardFixture.create(user2), user2))
        );
        return new PageImpl<>(responses, PageRequest.of(1, 50), responses.size());
    }

    private Page<BookBoardResponse> likePagesProvider() {
        List<BookBoardResponse> responses = List.of(
                new BookBoardResponse(BoardFixture.create(UserFixture.create("test2@gmail.com"))),
                new BookBoardResponse(BoardFixture.create(UserFixture.create("test3#gmail.com")))
        );
        return new PageImpl<>(responses, PageRequest.of(1, 50), responses.size());
    }
}
