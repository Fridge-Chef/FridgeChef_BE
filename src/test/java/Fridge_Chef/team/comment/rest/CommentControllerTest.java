package Fridge_Chef.team.comment.rest;

import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.comment.repository.CommentRepository;
import Fridge_Chef.team.comment.rest.request.CommentCreateRequest;
import Fridge_Chef.team.comment.rest.request.CommentUpdateRequest;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.comment.service.CommentService;
import Fridge_Chef.team.common.RestDocControllerTests;
import Fridge_Chef.team.common.auth.WithMockCustomUser;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@DisplayName("댓글 API")
@WebMvcTest(CommentController.class)
public class CommentControllerTest extends RestDocControllerTests {

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
        // Given
        CommentCreateRequest commentRequest = new CommentCreateRequest("댓글 내용", null, 4.5);
        String requestJson = objectMapper.writeValueAsString(commentRequest);

        // When
        ResultActions result = jwtJsonPostPathWhen("/api/boards/{board_id}/comments", requestJson, 1);

        // Then
        result.andExpect(status().isOk())
                .andDo(document("댓글 달기",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("comment").description("The content of the comment"),
                                fieldWithPath("image").description("Optional image attached to the comment"),
                                fieldWithPath("star").description("Rating out of 5 stars, allowing for half-star increments")
                        )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("수정")
    void testUpdateComment() throws Exception {
        // Given
        CommentUpdateRequest commentRequest = new CommentUpdateRequest("수정된 댓글 내용", null, 4.5);
        String requestJson = objectMapper.writeValueAsString(commentRequest);

        // When
        ResultActions result = jwtJsonPutPathWhen("/api/boards/{board_id}/comments/{comment_id}", requestJson, 1, 1);

        // Then
        result.andExpect(status().isOk())
                .andDo(document("댓글 수정",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("comment").description("수정된 댓글 내용"),
                                fieldWithPath("image").description("이미지"),
                                fieldWithPath("star").description("별점")
                        )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("삭제")
    void testDeleteComment() throws Exception {
        ResultActions result = jwtDeletePathWhen("/api/boards/{board_id}/comments/{comment_id}", 1, 1);

        result.andExpect(status().isOk())
                .andDo(document("댓글 삭제",
                        jwtTokenRequest()));
    }

    @Test
    @DisplayName("전체조회")
    void testGetAllComments() throws Exception {
        when(commentService.getCommentsByBoard(anyLong(), any(Optional.class))).thenReturn(getAllCommentsProvider());


        ResultActions result = jwtGetPathWhen("/api/boards/{board_id}/comments", 1);

        result.andExpect(status().isOk())
                .andDo(document("댓글조회",
                        responseFields(
                                fieldWithPath("[].id").description("댓글 ID"),
                                fieldWithPath("[].comment").description("댓글 내용"),
                                fieldWithPath("[].star").description("별점"),
                                fieldWithPath("[].userName").description("사용자 이름"),
                                fieldWithPath("[].imageLink").description("이미지 주소"),
                                fieldWithPath("[].boardId").description("게시판 ID"),
                                fieldWithPath("[].createdAt").description("작성일")
                        )));
    }

    private static List<CommentResponse> getAllCommentsProvider() {
        return List.of(
                new CommentResponse(1L, "댓글 내용", 4.5, "User1", "test.png", 1L, LocalDateTime.now()),
                new CommentResponse(2L, "또 다른 댓글", 5.0, "User2", "test.png", 1L, LocalDateTime.now())
        );
    }
}
