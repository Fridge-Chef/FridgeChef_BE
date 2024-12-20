package Fridge_Chef.team.comment.rest;

import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.comment.repository.CommentRepository;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.comment.service.CommentService;
import Fridge_Chef.team.common.docs.CustomPart;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static fixture.ImageFixture.partImage;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@DisplayName("댓글 API")
@WebMvcTest({CommentsController.class, CommentController.class})
public class CommentsControllerTest extends RestDocControllerTests {
    @MockBean
    private CommentService commentService;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private BoardRepository boardRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ImageService imageService;

    @Test
    @WithMockCustomUser
    @DisplayName("추가")
    void testAddComment() throws Exception {
        List<CustomPart> formData = List.of(
                partImage("images", "이미지 파일들", false),
                partImage("images", "이미지 파일들", false),
                part("comment", "댓글"),
                part("star", "4.5")
        );

        ResultActions actions = mockMvc.perform(jwtFormPostPathWhen("/api/boards/{board_id}", formData, 1));

        actions.andExpect(status().isOk())
                .andDo(document("후기 추가",
                        jwtTokenRequest(),
                        pathParameters(
                                parameterWithName("board_id").description("게시글 ID")
                        ), requestPartsForm(formData)
                ));
    }


    @Test
    @WithMockCustomUser
    @DisplayName("좋아요 클릭")
    void addLikeClick() throws Exception {
        ResultActions result = jwtPatchPathWhen("/api/boards/{board_id}/comments/{comment_id}/hit", 1, 1);

        result.andExpect(status().isOk())
                .andDo(document("좋아요 클릭",
                        jwtTokenRequest(),
                        pathParameters(
                                parameterWithName("board_id").description("게시글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        )
                ));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("수정")
    void testUpdateComment() throws Exception {
        List<CustomPart> formData = List.of(
                partImage("images", "이미지 파일들", false),
                partImage("images", "이미지 파일들", false),
                part("comment", "댓글"),
                part("star", "4.5"),
                part("isImage", "false")
        );

        ResultActions actions = mockMvc.perform(jwtFormPutPathWhen("/api/boards/{board_id}/comments/{comment_id}", formData, 1, 1));

        actions.andExpect(status().isOk())
                .andDo(document("후기 수정",
                                jwtTokenRequest(),
                                pathParameters(
                                        parameterWithName("board_id").description("게시글 ID"),
                                        parameterWithName("comment_id").description("댓글 ID")
                                ),
                                requestPartsForm(formData)
                        )
                );
    }

    @Test
    @WithMockCustomUser
    @DisplayName("삭제")
    void testDeleteComment() throws Exception {
        ResultActions result = jwtDeletePathWhen("/api/boards/{board_id}/comments/{comment_id}", 1, 1);

        result.andExpect(status().isOk())
                .andDo(document("후기 삭제",
                        jwtTokenRequest()));
    }

    @Test
    @DisplayName("전체조회")
    void testGetAllComments() throws Exception {
        when(commentService.getCommentsByBoards(anyLong(), anyInt(), anyInt(), any(Optional.class))).thenReturn(getAllCommentsProvider());

        ResultActions result = jwtGetPathWhen("/api/boards/{board_id}/comments?page=0&size=30", 1);

        result.andExpect(status().isOk())
                .andDo(document("후기 전체 조회",
                        pathParameters(
                                parameterWithName("board_id").description("게시글 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("현재 페이지 번호"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("content[]").description("후기 리스트"),
                                fieldWithPath("content[].id").description("ID"),
                                fieldWithPath("content[].title").description("레시피 명"),
                                fieldWithPath("content[].comments").description("후기 내용"),
                                fieldWithPath("content[].like").description("좋아요 수 "),
                                fieldWithPath("content[].myHit").description("내 좋아요 여부 "),
                                fieldWithPath("content[].myMe").description("내가 작성한 댓글 여부"),
                                fieldWithPath("content[].star").description("별점"),
                                fieldWithPath("content[].userName").description("사용자 이름"),
                                fieldWithPath("content[].imageLink[]").description("이미지 주소"),
                                fieldWithPath("content[].boardId").description("게시판 ID"),
                                fieldWithPath("content[].createdAt").description("작성일"),
                                fieldWithPath("page.size").description("페이지 크기"),
                                fieldWithPath("page.number").description("현재 페이지 번호"),
                                fieldWithPath("page.totalElements").description("전체 요소 수"),
                                fieldWithPath("page.totalPages").description("전체 페이지 수")
                        )));
    }

    @Test
    @DisplayName("단일 조회")
    void getComment() throws Exception {
        when(commentService.getCommentsByBoard(anyLong(), anyLong(), any(Optional.class)))
                .thenReturn(getAllCommentsProvider().getContent().get(0));

        ResultActions result = jwtGetPathWhen("/api/boards/{board_id}/comments/{comment_id}", 1, 1);

        result.andExpect(status().isOk())
                .andDo(document("후기 단일 조회",
                        pathParameters(
                                parameterWithName("board_id").description("게시글 ID"),
                                parameterWithName("comment_id").description("댓글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description(" ID"),
                                fieldWithPath("title").description("레시피 명"),
                                fieldWithPath("comments").description("내용"),
                                fieldWithPath("like").description("좋아요 수 "),
                                fieldWithPath("myHit").description("내 좋아요 여부"),
                                fieldWithPath("myMe").description("내가 작성한 댓글 여부"),
                                fieldWithPath("star").description("별점"),
                                fieldWithPath("userName").description("사용자 이름"),
                                fieldWithPath("imageLink[]").description("이미지 주소"),
                                fieldWithPath("boardId").description("게시판 ID"),
                                fieldWithPath("createdAt").description("작성일")
                        )));
    }

    private static Page<CommentResponse> getAllCommentsProvider() {
        return new PageImpl<>(List.of(
                new CommentResponse(1L, "레시피명", "후기 내용", 4.5, 1, false, "User1", false, List.of("test.png"), 1L, LocalDateTime.now()),
                new CommentResponse(2L, "레시피명", "또 다른 후기", 5.0, 1, false, "User2", false, List.of("test.png", "test2.png"), 1L, LocalDateTime.now())
        ), PageRequest.of(0, 10), 2);
    }
}
