package Fridge_Chef.team.comment.rest;

import Fridge_Chef.team.comment.rest.request.CommentUpdateRequest;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.comment.service.CommentService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/boards/{board_id}/comments")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentService commentService;


    @GetMapping
    public List<CommentResponse> getAllComments(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId) {
        return commentService.getCommentsByBoard(boardId, Optional.ofNullable(user));
    }

    @GetMapping("/{comment_id}")
    public CommentResponse getComments(
            @PathVariable("board_id") Long boardId,
            @PathVariable("comment_id") Long commentId) {
        return commentService.getCommentsByBoard(boardId, commentId);
    }


    @PutMapping("/{comment_id}")
    public void updateComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId,
            @PathVariable("comment_id") Long commentId,
            @Valid @ModelAttribute CommentUpdateRequest commentRequest) {
        commentService.updateComment(boardId, commentId, user.userId(), commentRequest);
    }

    @DeleteMapping("/{comment_id}")
    public void deleteComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId,
            @PathVariable("comment_id") Long commentId) {
        commentService.deleteComment(boardId, commentId, user.userId());
    }

    @PatchMapping("/{comment_id}/like")
    public void addLike(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId,
            @PathVariable("comment_id") Long commentId) {
        commentService.updateHit(boardId, commentId, user.userId());
    }
}