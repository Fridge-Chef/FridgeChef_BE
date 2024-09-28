package Fridge_Chef.team.board.rest;

import Fridge_Chef.team.board.rest.request.BoardPageRequest;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.board.service.response.BoardMyRecipePageResponse;
import Fridge_Chef.team.board.service.response.BoardMyRecipeResponse;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
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
    public Page<BoardMyRecipePageResponse> page(@Valid @RequestBody BoardPageRequest request) {
        return boardService.findMyRecipes(request);
    }

    @PostMapping("/{board_id}/hit")
    public void hit(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable("board_id") Long boardId) {
        boardService.updateUserHit(user.userId(),boardId);
    }
}
