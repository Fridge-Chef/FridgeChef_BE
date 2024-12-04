package Fridge_Chef.team.board.rest;

import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BookCommentRequest;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.board.service.BookService;
import Fridge_Chef.team.comment.rest.response.CommentResponse;
import Fridge_Chef.team.comment.service.CommentService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CommentService commentService;

    @GetMapping("/recipe")
    public Page<BookBoardResponse> selectLike(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "MYRECIPE", required = false) BookType book,
            @RequestParam(defaultValue = "LATEST", required = false) SortType sort) {
        return bookService.selectBoards(user.userId(), new BookRecipeRequest(page, size, book, sort));
    }

    @GetMapping("/comment")
    public Page<CommentResponse> selectComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int size,
            @RequestParam(defaultValue = "LATEST", required = false) SortType sort) {
        return bookService.selectComment(user.userId(), new BookCommentRequest(page, size, sort));
    }
}
