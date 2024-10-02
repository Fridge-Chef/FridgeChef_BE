package Fridge_Chef.team.board.rest;

import Fridge_Chef.team.board.rest.request.BookCommentRequest;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.board.rest.response.BookCommentResponse;
import Fridge_Chef.team.board.service.BookService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/recipe")
    public Page<BookBoardResponse> selectLike(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody BookRecipeRequest request) {
        return bookService.selectBoards(user.userId(), request);
    }


    @GetMapping("/comment")
    public Page<BookCommentResponse> selectComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody BookCommentRequest request) {
        return bookService.selectComment(user.userId(), request);
    }
}
