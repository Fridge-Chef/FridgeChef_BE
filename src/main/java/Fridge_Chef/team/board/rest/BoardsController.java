package Fridge_Chef.team.board.rest;

import Fridge_Chef.team.board.repository.model.IssueType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.rest.request.BoardStarRequest;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardsController {
    private final BoardService boardService;

    @GetMapping("/{board_id}")
    public BoardMyRecipeResponse targetFind(@PathVariable("board_id") Long boardId) {
        boardService.counting(boardId);
        return boardService.findMyRecipeId(boardId);
    }

    @GetMapping
    public Page<BoardMyRecipePageResponse> page(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "ALL", required = false) IssueType issue,
            @RequestParam(defaultValue = "LATEST", required = false) SortType sort) {
        UserId userId = openUserId(user);
        return boardService.findMyRecipes(userId, new BoardPageRequest(page, size, issue, sort));
    }

    @PostMapping("/{board_id}/hit")
    public void hit(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable("board_id") Long boardId) {
        boardService.updateUserHit(user.userId(), boardId);
    }

    @PostMapping("/{board_id}/star")
    public void star(@AuthenticationPrincipal AuthenticatedUser user, @PathVariable("board_id") Long boardId, BoardStarRequest request) {
        boardService.updateUserStar(user.userId(), boardId, request);
    }

    private static UserId openUserId(AuthenticatedUser user) {
        if (user == null) {
            return null;
        }
        return user.userId();
    }
}
