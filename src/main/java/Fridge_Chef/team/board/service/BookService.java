package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.repository.BookDslRepository;
import Fridge_Chef.team.board.rest.request.BookCommentRequest;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.board.rest.response.BookCommentResponse;
import Fridge_Chef.team.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookDslRepository bookDslRepository;

    public Page<BookBoardResponse> selectBoards(UserId userId, BookRecipeRequest request) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        return bookDslRepository.findByBoard(pageable, userId, request);
    }

    public Page<BookCommentResponse> selectComment(UserId userId, BookCommentRequest request) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        return bookDslRepository.findByComment(pageable, userId, request);
    }
}
