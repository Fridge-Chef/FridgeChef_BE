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

import java.util.List;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

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
        BookRecipeRequest bookBoardResponses = new BookRecipeRequest(BookType.LIKE, SortType.LATEST, 1, 50);

        String request = objectMapper.writeValueAsString(bookBoardResponses);

        when(bookService.selectBoards(any(UserId.class), any(BookRecipeRequest.class)))
                .thenReturn(likePagesProvider());

        ResultActions actions = jwtJsonGetWhen("/api/books/recipe", request);

        actions.andExpect(status().isOk())
                .andDo(document("좋아요",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("bookType").description("북 타입 : (좋아요,나만의레시피,후기) [LIKE,MYRECIPE,LIKE]"),
                                fieldWithPath("page").description("페이지 번호 "),
                                fieldWithPath("size").description("사이즈 크기 "),
                                fieldWithPath("sortType").description("정렬 타입 :(최신순,별점순,클릭순,좋아요순) [ LATEST, RATING , CLICKS, HIT ] ")
                        ),
                        responseFields(
                                fieldWithPath("totalPages").description("총 페이지 수"),
                                fieldWithPath("totalElements").description("총 요소 수"),
                                fieldWithPath("size").description("페이지당 요소 수"),
                                fieldWithPath("content[]").description("책 목록"),
                                fieldWithPath("content[].id").description("ID 게시글"),
                                fieldWithPath("content[].mainImageLink").description("이미지 주소"),
                                fieldWithPath("content[].title").description("레시피 이름"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].hit").description("하트"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort.empty").description("정렬이 비었는지 여부"),
                                fieldWithPath("sort.sorted").description("정렬 여부"),
                                fieldWithPath("sort.unsorted").description("정렬되지 않은 여부"),
                                fieldWithPath("pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty").description("페이지 정렬이 비었는지 여부"),
                                fieldWithPath("pageable.sort.sorted").description("페이지가 정렬되었는지 여부"),
                                fieldWithPath("pageable.sort.unsorted").description("페이지가 정렬되지 않았는지 여부"),
                                fieldWithPath("pageable.offset").description("페이지 오프셋"),
                                fieldWithPath("pageable.paged").description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").description("페이징되지 않았는지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                fieldWithPath("first").description("첫 번째 페이지 여부"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("나의 레시피 페이징 조회")
    void findMyRecipe() throws Exception {
        BookRecipeRequest bookBoardResponses = new BookRecipeRequest(BookType.MYRECIPE, SortType.LATEST, 1, 50);

        String request = objectMapper.writeValueAsString(bookBoardResponses);

        when(bookService.selectBoards(any(UserId.class), any(BookRecipeRequest.class)))
                .thenReturn(likePagesProvider());

        ResultActions actions = jwtJsonGetWhen("/api/books/recipe", request);

        actions.andExpect(status().isOk())
                .andDo(document("나만의 레시피,",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("bookType").description("북 타입 : (좋아요,나만의레시피,후기) [LIKE,MYRECIPE,LIKE]"),
                                fieldWithPath("page").description("페이지 번호 "),
                                fieldWithPath("size").description("사이즈 크기 "),
                                fieldWithPath("sortType").description("정렬 타입 :(최신순,별점순,클릭순,좋아요순) [ LATEST, RATING , CLICKS, HIT ] ")
                        ),
                        responseFields(
                                fieldWithPath("totalPages").description("총 페이지 수"),
                                fieldWithPath("totalElements").description("총 요소 수"),
                                fieldWithPath("size").description("페이지당 요소 수"),
                                fieldWithPath("content[]").description("책 목록"),
                                fieldWithPath("content[].id").description("ID 게시글"),
                                fieldWithPath("content[].mainImageLink").description("이미지 주소"),
                                fieldWithPath("content[].title").description("레시피 이름"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].hit").description("하트"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort.empty").description("정렬이 비었는지 여부"),
                                fieldWithPath("sort.sorted").description("정렬 여부"),
                                fieldWithPath("sort.unsorted").description("정렬되지 않은 여부"),
                                fieldWithPath("pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty").description("페이지 정렬이 비었는지 여부"),
                                fieldWithPath("pageable.sort.sorted").description("페이지가 정렬되었는지 여부"),
                                fieldWithPath("pageable.sort.unsorted").description("페이지가 정렬되지 않았는지 여부"),
                                fieldWithPath("pageable.offset").description("페이지 오프셋"),
                                fieldWithPath("pageable.paged").description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").description("페이징되지 않았는지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                fieldWithPath("first").description("첫 번째 페이지 여부"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("empty").description("페이지가 비어 있는지 여부")
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("레시피 후기 페이징 조회")
    void findComments() throws Exception {
        BookCommentRequest bookCommentRequest = new BookCommentRequest(SortType.LATEST, 1, 50);  // 페이지 요청 생성
        String request = objectMapper.writeValueAsString(bookCommentRequest);

        when(bookService.selectComment(any(UserId.class), any(BookCommentRequest.class)))
                .thenReturn(commentPagesProvider());

        ResultActions actions = jwtJsonGetWhen("/api/books/comment", request);

        actions.andExpect(status().isOk())
                .andDo(document("레시피 후기",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("page").description("페이지 번호 "),
                                fieldWithPath("size").description("사이즈 크기 "),
                                fieldWithPath("sortType").description("정렬 타입 :(최신순,별점순,클릭순,좋아요순) [ LATEST, RATING , CLICKS, HIT ] ")
                        ),
                        responseFields(
                                fieldWithPath("totalPages").description("총 페이지 수"),
                                fieldWithPath("totalElements").description("총 요소 수"),
                                fieldWithPath("size").description("페이지당 요소 수"),
                                fieldWithPath("content[]").description("코멘트 목록"),
                                fieldWithPath("content[].boardId").description("게시글 ID"),
                                fieldWithPath("content[].commentId").description("코멘트 ID"),
                                fieldWithPath("content[].name").description("작성자 이름"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].context").description("코멘트 내용"),
                                fieldWithPath("number").description("현재 페이지 번호"),
                                fieldWithPath("sort.empty").description("정렬이 비었는지 여부"),
                                fieldWithPath("sort.sorted").description("정렬 여부"),
                                fieldWithPath("sort.unsorted").description("정렬되지 않은 여부"),
                                fieldWithPath("pageable.pageNumber").description("페이지 번호"),
                                fieldWithPath("pageable.pageSize").description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty").description("페이지 정렬이 비었는지 여부"),
                                fieldWithPath("pageable.sort.sorted").description("페이지가 정렬되었는지 여부"),
                                fieldWithPath("pageable.sort.unsorted").description("페이지가 정렬되지 않았는지 여부"),
                                fieldWithPath("pageable.offset").description("페이지 오프셋"),
                                fieldWithPath("pageable.paged").description("페이징 여부"),
                                fieldWithPath("pageable.unpaged").description("페이징되지 않았는지 여부"),
                                fieldWithPath("numberOfElements").description("현재 페이지의 요소 수"),
                                fieldWithPath("first").description("첫 번째 페이지 여부"),
                                fieldWithPath("last").description("마지막 페이지 여부"),
                                fieldWithPath("empty").description("페이지가 비어 있는지 여부")
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
