package Fridge_Chef.team.comment.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.repository.CommentRepository;
import Fridge_Chef.team.comment.rest.request.CommentCreateRequest;
import Fridge_Chef.team.comment.rest.request.CommentUpdateRequest;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import fixture.BoardFixture;
import fixture.ImageFixture;
import fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("댓글 서비스")
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private CommentService commentService;

    private Board board;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = UserFixture.create("test@email.com");
        board = BoardFixture.create(user);
        board.updateId(1L);
        comment = new Comment(board, user, List.of(), "Test Comment", 4.0);
        comment.updateId(1L);
    }

    @Test
    @Transactional
    @DisplayName("댓글 추가 - 성공")
    void addComment_Success() {
        CommentCreateRequest request = new CommentCreateRequest("Test Comment", null, 4.0);
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(userRepository.findByUserId_Value(any(UUID.class))).thenReturn(Optional.of(user));
//        when(imageService.imageUploads(any(UserId.class), isNull())).thenReturn(List.of(ImageFixture.create()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.addComment(1L, UserFixture.create("tests@gmail.com").getUserId(), request);

        assertNotNull(result);
        assertEquals("Test Comment", result.getComments());
        assertEquals(4.0, result.getStar());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @Transactional
    @DisplayName("댓글 수정 - 성공")
    void updateComment_Success() {
        CommentUpdateRequest request = new CommentUpdateRequest("test", false, null, 4.5);
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        Comment result = commentService.updateComment(1L, 1L, user.getUserId(), request);

        assertNotNull(result);
        assertEquals(4.5, result.getStar());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @Transactional
    @DisplayName("댓글 삭제 - 성공")
    void deleteComment_Success() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        commentService.deleteComment(board.getId(), comment.getId(), user.getUserId());

        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @Test
    @Transactional(readOnly = true)
    @DisplayName("게시판 별 댓글 조회 - 성공")
    void getCommentsByBoard_Success() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(commentRepository.findAllByBoard(any(Board.class))).thenReturn(List.of(comment));

        Page<CommentResponse> comments = commentService.getCommentsByBoards(1L, 0,50, Optional.of(new AuthenticatedUser(UserId.create(), Role.USER).userId()));

        assertNotNull(comments);
        assertEquals(1, comments.getTotalElements());
    }

    @Test
    @DisplayName("게시판을 찾을 수 없을 때 댓글 추가 - 실패")
    void addComment_BoardNotFound() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> commentService.addComment(1L, UserFixture.create("tests@gmail.com").getUserId(), new CommentCreateRequest("Test", null, 5.0)));
        assertEquals(ErrorCode.BOARD_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글을 찾을 수 없을 때 댓글 수정 - 실패")
    void updateComment_CommentNotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class, () -> commentService.updateComment(1L, 1L, UserFixture.create("tests@gmail.com").getUserId(), new CommentUpdateRequest("test", false, null, 4.0)));
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

}
