package Fridge_Chef.team.comment.rest;

import Fridge_Chef.team.comment.rest.request.CommentCreateRequest;
import Fridge_Chef.team.comment.service.CommentService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards/{board_id}")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void addComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable("board_id") Long boardId,
            @Valid @ModelAttribute CommentCreateRequest commentRequest) {
        commentService.addComment(boardId, user.userId(), commentRequest);
    }
}