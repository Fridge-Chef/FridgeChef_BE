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
                .andDo(document("후기 추가",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("comment").description("내용"),
                                fieldWithPath("image").description("이미지 리스트 "),
                                fieldWithPath("star").description("별점")
                        )));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("좋아요 클릭")
    void addLikeClick() throws Exception {
        // When
        ResultActions result = jwtPatchPathWhen("/api/boards/{board_id}/comments/{comment_id}/like", 1,1);

        // Then
        result.andExpect(status().isOk())
                .andDo(document("좋아요 클릭",
                        jwtTokenRequest()));
    }

    @Test
    @WithMockCustomUser
    @DisplayName("수정")
    void testUpdateComment() throws Exception {
        // Given
        CommentUpdateRequest commentRequest = new CommentUpdateRequest("수정된 댓글 내용", false,null, 4.5);
        String requestJson = objectMapper.writeValueAsString(commentRequest);

        // When
        ResultActions result = jwtJsonPutPathWhen("/api/boards/{board_id}/comments/{comment_id}", requestJson, 1, 1);

        // Then
        result.andExpect(status().isOk())
                .andDo(document("후기 수정",
                        jwtTokenRequest(),
                        requestFields(
                                fieldWithPath("comment").description("수정된 후기 내용"),
                                fieldWithPath("isImage").description("이미지 수정 여부"),
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
                .andDo(document("후기 삭제",
                        jwtTokenRequest()));
    }

    @Test
    @DisplayName("전체조회")
    void testGetAllComments() throws Exception {
        when(commentService.getCommentsByBoard(anyLong(), any(Optional.class))).thenReturn(getAllCommentsProvider());


        ResultActions result = jwtGetPathWhen("/api/boards/{board_id}/comments", 1);

        result.andExpect(status().isOk())
                .andDo(document("후기 조회",
                        responseFields(
                                fieldWithPath("[]").description("후기 리스트"),
                                fieldWithPath("[].id").description("후기 ID"),
                                fieldWithPath("[].comment").description("후기 내용"),
                                fieldWithPath("[].like").description("좋아요 수 "),
                                fieldWithPath("[].star").description("별점"),
                                fieldWithPath("[].userName").description("사용자 이름"),
                                fieldWithPath("[].imageLink[]").description("이미지 주소"),
                                fieldWithPath("[].boardId").description("게시판 ID"),
                                fieldWithPath("[].createdAt").description("작성일")
                        )));
    }

    private static List<CommentResponse> getAllCommentsProvider() {
        return List.of(
                new CommentResponse(1L, "후기 내용", 4.5,1, "User1", List.of("test.png"), 1L, LocalDateTime.now()),
                new CommentResponse(2L, "또 다른 후기", 5.0, 1,"User2", List.of("test.png","test2.png"), 1L, LocalDateTime.now())
        );
    }
}
