package Fridge_Chef.team.board.repository;


import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import Fridge_Chef.team.board.rest.request.BookRecipeRequest;
import Fridge_Chef.team.board.rest.response.BookBoardResponse;
import Fridge_Chef.team.common.JpaTest;
import Fridge_Chef.team.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class BookDslRepositoryTest extends JpaTest {
    private BookDslRepository bookDslRepository;

    @BeforeEach
    void setup() {
        bookDslRepository = new BookDslRepository(factory);
    }

    @Test
    void testFindByBoard() {
        userBoardCommentMetaData();
        BookRecipeRequest request = new BookRecipeRequest(BookType.MYRECIPE, SortType.LATEST, 0, 50);
        User user = userRepository.findAll().get(0);
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<BookBoardResponse> result = bookDslRepository.findByBoard(pageable, user.getUserId(), request);

        assertThat(result.getContent().size()).isNotEqualTo(0);
        System.out.println(result.getContent().size());
    }
}
