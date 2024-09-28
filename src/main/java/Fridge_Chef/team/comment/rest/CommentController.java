package Fridge_Chef.team.comment.rest;

import Fridge_Chef.team.comment.rest.request.CommentCreateRequest;
import Fridge_Chef.team.comment.rest.request.CommentUpdateRequest;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.comment.service.CommentService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards/{board_id}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public void addComment(@AuthenticationPrincipal AuthenticatedUser user,
                           @PathVariable("board_id") Long boardId,
                           @RequestBody CommentCreateRequest commentRequest) {
        commentService.addComment(boardId, user.userId(), commentRequest);
    }

    @PutMapping("/{comment_id}")
    public void updateComment(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId,
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentUpdateRequest commentRequest) {
        commentService.updateComment(boardId,commentId,user.userId(), commentRequest);
    }

    @DeleteMapping("/{comment_id}")
    public void deleteComment(@AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId,
            @PathVariable("comment_id") Long commentId) {
        commentService.deleteComment(boardId, commentId,user.userId());
    }


    @GetMapping
    public List<CommentResponse> getAllComments(@PathVariable("board_id") Long boardId) {
        return commentService.getCommentsByBoard(boardId);
    }
}